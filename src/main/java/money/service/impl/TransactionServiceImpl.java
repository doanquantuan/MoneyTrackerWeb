package money.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Account;
import money.entity.Category;
import money.entity.Transaction;
import money.entity.User;
import money.enums.CategoryType;
import money.enums.TransactionType;
import org.springframework.context.ApplicationEventPublisher;
import money.enums.NotificationType;
import money.service.impl.notif.GenericNotificationEvent;
import money.repository.AccountRepository;
import money.repository.CategoryRepository;
import money.repository.TransactionRepository;
import money.repository.UserRepository;
import money.repository.BudgetJarRepository;
import money.service.IBudgetJarService;
import money.service.ITransactionService;
import money.entity.BudgetJar;

@Service
@Transactional
public class TransactionServiceImpl implements ITransactionService{
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private BudgetJarRepository budgetJarRepo;

	@Autowired
	private IBudgetJarService budgetJarService;

	@Override
	public Transaction createStandardTransaction(String email, TransactionInExRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tìm thấy"));
		
		Category cate = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy"));
		
		if (!cate.getCategoryType().equals(CategoryType.valueOf(request.getType().toUpperCase()))) {
			throw new RuntimeException("Loại danh mục không phù hợp với giao dịch này");
		}
		
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(account);
		trans.setToAccount(null);
		trans.setCategory(cate);
		trans.setType(TransactionType.valueOf(request.getType().toUpperCase()));
		trans.setAmount(request.getAmount());
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote(request.getNote());
		
		if ("INCOME".equals(request.getType().toUpperCase())) {
			account.setCurrentBalance(account.getCurrentBalance() + request.getAmount());
		} else if ("EXPENSE".equals(request.getType().toUpperCase())) {
			account.setCurrentBalance(account.getCurrentBalance() - request.getAmount());
		}
		
		accountRepo.save(account);
		applyJarBalance(trans, request.getBudgetJarId(), request.getAutoAllocateToJars());
		
		Transaction savedTx = transactionRepo.save(trans);
		
		return savedTx;
	}

	@Override
	public Transaction transferMoney(String email, TransferRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		if (request.getAccountId().equals(request.getToAccountId())) {
			throw new RuntimeException("Không thể tự chuyển cho chính tài khoản này");
		}
		
		Account fromAccount = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản chuyển tiền không tìm thấy"));
		
		Account toAccount = accountRepo.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản nhận tiền không tìm thấy"));
		
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(fromAccount);
		trans.setToAccount(toAccount);
		trans.setCategory(null);
		trans.setType(TransactionType.valueOf(request.getType().toUpperCase()));
		trans.setAmount(request.getAmount());
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote(request.getNote());
		
		fromAccount.setCurrentBalance(fromAccount.getCurrentBalance() - request.getAmount());
		toAccount.setCurrentBalance(toAccount.getCurrentBalance() + request.getAmount());
		accountRepo.save(fromAccount);
		accountRepo.save(toAccount);
		
		return transactionRepo.save(trans);
	}

	@Override
	public Transaction recordDebtTransaction() {
		return null;
	}

	@Override
	public Transaction getTransactionById(String email, long id) {
		Transaction tx = transactionRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));
		if (!tx.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xem giao dịch này");
		}
		return tx;
	}

	@Override
	@Transactional
	public Transaction updateTransaction(String email, long id, TransactionInExRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tìm thấy"));
		
		Category cate = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy"));
		
		if (!cate.getCategoryType().equals(CategoryType.valueOf(request.getType().toUpperCase()))) {
			throw new RuntimeException("Loại danh mục không phù hợp với giao dịch này");
		}
		
		Transaction oldTx = transactionRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));
		
		if (!oldTx.getType().equals(TransactionType.valueOf(request.getType().toUpperCase()))) {
			throw new RuntimeException("Không thể thay đổi TransactionType");
		}
		
		revertBalance(oldTx);
		revertJarBalance(oldTx);
		
		oldTx.setAmount(request.getAmount());
	    oldTx.setNote(request.getNote());
	    oldTx.setCategory(cate);
	    oldTx.setAccount(account);
	    
	    applyBalance(oldTx);
	    applyJarBalance(oldTx, request.getBudgetJarId(), request.getAutoAllocateToJars());

	    return transactionRepo.save(oldTx);
	}
	
	@Override
	@Transactional
	public Transaction updateTransfer(String email, long id, TransferRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		if (request.getAccountId().equals(request.getToAccountId())) {
			throw new RuntimeException("Không thể tự chuyển cho chính tài khoản này");
		}
		
		Account fromAccount = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản chuyển tiền không tìm thấy"));
		
		Account toAccount = accountRepo.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản nhận tiền không tìm thấy"));
		
		Transaction oldTx = transactionRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));
		
		if (!oldTx.getType().equals(TransactionType.valueOf(request.getType().toUpperCase()))) {
			throw new RuntimeException("Không thể thay đổi TransactionType");
		}
		
		revertTransferBalance(oldTx);

	    oldTx.setAmount(request.getAmount());
	    oldTx.setNote(request.getNote());
	    oldTx.setAccount(fromAccount);   
	    oldTx.setToAccount(toAccount);   

	    applyTransferBalance(oldTx);

	    return transactionRepo.save(oldTx);
	}

	@Override
	@Transactional
	public void deleteTransaction(String email, long id) {
		Transaction tx = transactionRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));
		if (!tx.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xóa giao dịch này");
		}
		
		if (tx.getType() == TransactionType.DEBT_LOAN || tx.getType() == TransactionType.DEBT_REPAYMENT) {
			throw new RuntimeException("Không thể xóa trực tiếp giao dịch nợ/trả nợ. Vui lòng quản lý qua mục Khoản Nợ.");
		}
		
		// Revert balance before deleting
		if (tx.getType() == TransactionType.INCOME) {
			Account account = tx.getAccount();
			account.setCurrentBalance(account.getCurrentBalance() - tx.getAmount());
			accountRepo.save(account);
		} else if (tx.getType() == TransactionType.EXPENSE) {
			Account account = tx.getAccount();
			account.setCurrentBalance(account.getCurrentBalance() + tx.getAmount());
			accountRepo.save(account);
		} else if (tx.getType() == TransactionType.TRANSFER) {
			Account fromAccount = tx.getAccount();
			Account toAccount = tx.getToAccount();
			fromAccount.setCurrentBalance(fromAccount.getCurrentBalance() + tx.getAmount());
			toAccount.setCurrentBalance(toAccount.getCurrentBalance() - tx.getAmount());
			accountRepo.save(fromAccount);
			accountRepo.save(toAccount);
		}
		
		revertJarBalance(tx);
		
		transactionRepo.delete(tx);
	}

	@Override
	public List<Transaction> searchTransactions(
		String email, Long accountId, Long categoryId, String type, 
		LocalDateTime startDate, LocalDateTime endDate
	) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		TransactionType txType = null;
		if (type != null && !type.trim().isEmpty()) {
			try {
				txType = TransactionType.valueOf(type.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Loại giao dịch không hợp lệ: " + type);
			}
		}
		
		return transactionRepo.searchTransactions(user, accountId, categoryId, txType, startDate, endDate);
	}

	@Override
	public List<Transaction> getListRecentTransaction(String email) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		return transactionRepo.findByUserOrderByTransactionDateDesc(user);
	}
	
	private void applyBalance(Transaction tx) {
	    Account account = tx.getAccount();
	    double amount = tx.getAmount();
	    
	    if (tx.getCategory().getCategoryType() == CategoryType.EXPENSE) {
	        account.setCurrentBalance(account.getCurrentBalance() - amount);
	    } else if (tx.getCategory().getCategoryType() == CategoryType.INCOME) {
	    	account.setCurrentBalance(account.getCurrentBalance() + amount);
	    }
	    accountRepo.save(account);
	}

	private void revertBalance(Transaction tx) {
	    Account account = tx.getAccount();
	    double amount = tx.getAmount();
	    
	    if (tx.getCategory().getCategoryType() == CategoryType.EXPENSE) {
	    	account.setCurrentBalance(account.getCurrentBalance() + amount);
	    } else if (tx.getCategory().getCategoryType() == CategoryType.INCOME) {
	    	account.setCurrentBalance(account.getCurrentBalance() - amount);
	    }
	    accountRepo.save(account);
	}

	private void applyTransferBalance(Transaction tx) {
	    Account from = tx.getAccount();
	    Account to = tx.getToAccount();
	    Double amount = tx.getAmount();

	    if (from.getCurrentBalance() < amount) {
	        throw new RuntimeException("Ví gửi không đủ số dư!");
	    }

	    from.setCurrentBalance(from.getCurrentBalance() - amount);
	    to.setCurrentBalance(to.getCurrentBalance() + amount);

	    accountRepo.save(from);
	    accountRepo.save(to);
	}

	private void revertTransferBalance(Transaction tx) {
		Account from = tx.getAccount();
	    Account to = tx.getToAccount();
	    Double amount = tx.getAmount();

	    from.setCurrentBalance(from.getCurrentBalance() + amount);
	    to.setCurrentBalance(to.getCurrentBalance() - amount);

	    accountRepo.save(from);
	    accountRepo.save(to);
	}

	private void revertJarBalance(Transaction tx) {
		if (tx.getType() == TransactionType.INCOME) {
			if (Boolean.TRUE.equals(tx.getAutoAllocateToJars())) {
				List<BudgetJar> jars = budgetJarRepo.findByUser_Email(tx.getUser().getEmail());
				for (BudgetJar j : jars) {
					double allocated = tx.getAmount() * (j.getPercentage() / 100.0);
					j.setAllocatedAmount(Math.max(0.0, j.getAllocatedAmount() - allocated));
					j.setRemainingAmount(j.getAllocatedAmount() - j.getSpentAmount());
					budgetJarRepo.save(j);
				}
			} else if (tx.getBudgetJar() != null) {
				BudgetJar j = tx.getBudgetJar();
				j.setAllocatedAmount(Math.max(0.0, j.getAllocatedAmount() - tx.getAmount()));
				j.setRemainingAmount(j.getAllocatedAmount() - j.getSpentAmount());
				budgetJarRepo.save(j);
			}
		} else if (tx.getType() == TransactionType.EXPENSE) {
			if (tx.getBudgetJar() != null) {
				BudgetJar j = tx.getBudgetJar();
				j.setSpentAmount(Math.max(0.0, j.getSpentAmount() - tx.getAmount()));
				j.setRemainingAmount(j.getAllocatedAmount() - j.getSpentAmount());
				budgetJarRepo.save(j);
			}
		}
	}

	private void applyJarBalance(Transaction tx, Long requestJarId, Boolean autoAllocateToJars) {
		if (tx.getType() == TransactionType.INCOME) {
			if (Boolean.TRUE.equals(autoAllocateToJars)) {
				tx.setAutoAllocateToJars(true);
				tx.setBudgetJar(null);
				List<BudgetJar> jars = budgetJarService.getJarsByUser(tx.getUser().getEmail());
				for (BudgetJar j : jars) {
					double allocated = tx.getAmount() * (j.getPercentage() / 100.0);
					j.setAllocatedAmount(j.getAllocatedAmount() + allocated);
					j.setRemainingAmount(j.getAllocatedAmount() - j.getSpentAmount());
					budgetJarRepo.save(j);
				}
			} else if (requestJarId != null) {
				BudgetJar jar = budgetJarRepo.findById(requestJarId)
						.orElseThrow(() -> new RuntimeException("Không tìm thấy chiếc lọ"));
				if (!jar.getUser().getEmail().equals(tx.getUser().getEmail())) {
					throw new RuntimeException("Bạn không có quyền sử dụng chiếc lọ này");
				}
				tx.setBudgetJar(jar);
				tx.setAutoAllocateToJars(false);
				jar.setAllocatedAmount(jar.getAllocatedAmount() + tx.getAmount());
				jar.setRemainingAmount(jar.getAllocatedAmount() - jar.getSpentAmount());
				budgetJarRepo.save(jar);
			} else {
				tx.setAutoAllocateToJars(false);
				tx.setBudgetJar(null);
			}
		} else if (tx.getType() == TransactionType.EXPENSE) {
			tx.setAutoAllocateToJars(false);
			if (requestJarId != null) {
				BudgetJar jar = budgetJarRepo.findById(requestJarId)
						.orElseThrow(() -> new RuntimeException("Không tìm thấy chiếc lọ"));
				if (!jar.getUser().getEmail().equals(tx.getUser().getEmail())) {
					throw new RuntimeException("Bạn không có quyền sử dụng chiếc lọ này");
				}
				tx.setBudgetJar(jar);
				double oldSpent = jar.getSpentAmount();
				double newSpent = oldSpent + tx.getAmount();
				jar.setSpentAmount(newSpent);
				jar.setRemainingAmount(jar.getAllocatedAmount() - jar.getSpentAmount());
				budgetJarRepo.save(jar);

				// Send warning notification if budget jar limit is exceeded
				if (newSpent > jar.getAllocatedAmount() && oldSpent <= jar.getAllocatedAmount()) {
					Map<String, Object> payload = new HashMap<>();
					payload.put("jar", jar);
					payload.put("newSpent", newSpent);
					eventPublisher.publishEvent(new GenericNotificationEvent(NotificationType.BUDGET, tx.getUser(), payload));
				}
			} else {
				tx.setBudgetJar(null);
			}
		}
	}
}
