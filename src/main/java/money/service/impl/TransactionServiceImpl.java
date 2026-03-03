package money.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Account;
import money.entity.Category;
import money.entity.Transaction;
import money.entity.User;
import money.enums.CategoryType;
import money.enums.TransactionType;
import money.repository.AccountRepository;
import money.repository.CategoryRepository;
import money.repository.TransactionRepository;
import money.repository.UserRepository;
import money.service.ITransactionService;

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
		
		if (request.getType().toUpperCase() == "INCOME") {
			account.setCurrentBalance(account.getCurrentBalance() + request.getAmount());
		} else {
			account.setCurrentBalance(account.getCurrentBalance() - request.getAmount());
		}
		
		accountRepo.save(account);
		
		return transactionRepo.save(trans);
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
		toAccount.setCurrentBalance(fromAccount.getCurrentBalance() + request.getAmount());
		accountRepo.save(fromAccount);
		accountRepo.save(toAccount);
		
		return transactionRepo.save(trans);
	}

	@Override
	public Transaction recordDebtTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getTransactionById() {
		// TODO Auto-generated method stub
		
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
		
		oldTx.setAmount(request.getAmount());
	    oldTx.setNote(request.getNote());
	    
	    oldTx.setCategory(cate);
	    
	    applyBalance(oldTx);

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
	public void deleteTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void searchTransactions() {
		// TODO Auto-generated method stub
		
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

}
