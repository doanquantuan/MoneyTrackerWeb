package money.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.debt.DebtRepaymentRequest;
import money.dto.debt.DebtRequest;
import money.entity.Account;
import money.entity.Debt;
import money.entity.DebtRepayment;
import money.entity.Transaction;
import money.entity.User;
import money.enums.DebtStatus;
import money.enums.DebtType;
import money.enums.InterestType;
import money.enums.TransactionType;
import money.repository.AccountRepository;
import money.repository.DebtRepaymentRepository;
import money.repository.DebtRepository;
import money.repository.TransactionRepository;
import money.repository.UserRepository;
import money.service.IDebtService;

@Service
public class DebtServiceImpl implements IDebtService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private DebtRepository debtRepo;
	
	@Autowired
	private DebtRepaymentRepository debtRepaymentRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;

	@Override
	public List<Debt> getListDebt(String email) {
		return debtRepo.findByUser_Email(email);
	}

	@Override
	public Debt getDebtById(String email, Long id) {
		Debt debt = debtRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy khoản nợ"));
		if (!debt.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xem khoản nợ này");
		}
		return debt;
	}

	@Override
	@Transactional
	public Debt addDebt(String email, DebtRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account không tìm thấy"));
		
		Debt debt = new Debt();
		
		debt.setUser(user);
		debt.setAccount(account);
		debt.setPartnerName(request.getPartnerName());
		
		DebtType dType = DebtType.valueOf(request.getType().toUpperCase());
		debt.setType(dType);
		debt.setPrincipalAmount(request.getPrincipalAmount());
		debt.setInterestRate(request.getInterestRate());
		debt.setInterestType(InterestType.valueOf(request.getInterestType().toUpperCase()));
		debt.setStartDate(request.getStartDate() == null ? LocalDateTime.now() : request.getStartDate());
		debt.setDueDate(request.getDueDate());
		debt.setStatus(DebtStatus.ACTIVE);
		
		if (dType == DebtType.LEND) {
		    if (account.getCurrentBalance() < request.getPrincipalAmount()) {
		        throw new RuntimeException("Số tiền trong tài khoản không đủ để thực hiện cho vay");
		    }
		    account.setCurrentBalance(account.getCurrentBalance() - request.getPrincipalAmount());
		} else if (dType == DebtType.BORROW) {
		    account.setCurrentBalance(account.getCurrentBalance() + request.getPrincipalAmount());
		}
		accountRepo.save(account);
		
		// Create associated transaction
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(account);
		trans.setCategory(null);
		trans.setType(TransactionType.DEBT_LOAN);
		trans.setAmount(request.getPrincipalAmount());
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote((dType == DebtType.LEND ? "Cho vay: " : "Vay từ: ") + request.getPartnerName());
		transactionRepo.save(trans);
		
		return debtRepo.save(debt);
	}

	@Override
	@Transactional
	public DebtRepayment repayDebt(String email, Long debtId, DebtRepaymentRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Debt debt = debtRepo.findById(debtId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy khoản nợ"));
		
		if (!debt.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền thực hiện giao dịch cho khoản nợ này");
		}
		
		if (debt.getStatus() == DebtStatus.COMPLETED || debt.getStatus() == DebtStatus.CANCELLED) {
			throw new RuntimeException("Khoản nợ đã tất toán hoặc đã bị hủy");
		}
		
		Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account không tìm thấy"));
		
		double amountPaid = request.getAmountPaid();
		double principalComp = request.getPrincipalComponent() != null ? request.getPrincipalComponent() : amountPaid;
		double interestComp = request.getInterestComponent() != null ? request.getInterestComponent() : 0.0;
		
		// Calculate current remaining balance of the debt
		double totalPrincipalRepaid = 0.0;
		if (debt.getRepayments() != null) {
			totalPrincipalRepaid = debt.getRepayments().stream()
					.mapToDouble(r -> r.getPrincipalComponent() != null ? r.getPrincipalComponent() : 0.0)
					.sum();
		}
		
		double remainingPrincipal = debt.getPrincipalAmount() - totalPrincipalRepaid;
		
		if (principalComp > remainingPrincipal) {
			throw new RuntimeException("Số tiền trả nợ gốc vượt quá dư nợ còn lại (" + remainingPrincipal + ")");
		}
		
		double newRemainingBalance = remainingPrincipal - principalComp;
		
		// Adjust account balance
		if (debt.getType() == DebtType.BORROW) {
			// We borrowed, now we repay. Balance decreases.
			if (account.getCurrentBalance() < amountPaid) {
				throw new RuntimeException("Số dư tài khoản không đủ để thực hiện thanh toán");
			}
			account.setCurrentBalance(account.getCurrentBalance() - amountPaid);
		} else if (debt.getType() == DebtType.LEND) {
			// We lent, now they repay us. Balance increases.
			account.setCurrentBalance(account.getCurrentBalance() + amountPaid);
		}
		accountRepo.save(account);
		
		// Record transaction
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(account);
		trans.setCategory(null);
		trans.setType(TransactionType.DEBT_REPAYMENT);
		trans.setAmount(amountPaid);
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote((debt.getType() == DebtType.BORROW ? "Trả nợ cho: " : "Thu hồi nợ từ: ") + debt.getPartnerName()
				+ (request.getNote() != null ? " - " + request.getNote() : ""));
		transactionRepo.save(trans);
		
		// Save repayment details
		DebtRepayment repayment = new DebtRepayment();
		repayment.setDebt(debt);
		repayment.setTransaction(trans);
		repayment.setRepaymentDate(LocalDateTime.now());
		repayment.setAmountPaid(amountPaid);
		repayment.setPrincipalComponent(principalComp);
		repayment.setInterestComponent(interestComp);
		repayment.setRemainingBalance(newRemainingBalance);
		DebtRepayment savedRepayment = debtRepaymentRepo.save(repayment);
		
		// Update debt status if completely repaid
		if (newRemainingBalance <= 0) {
			debt.setStatus(DebtStatus.COMPLETED);
			debtRepo.save(debt);
		}
		
		return savedRepayment;
	}

	@Override
	@Transactional
	public void deleteDebt(String email, Long id) {
		Debt debt = debtRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy khoản nợ"));
		
		if (!debt.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xóa khoản nợ này");
		}
		
		debtRepo.delete(debt);
	}
}
