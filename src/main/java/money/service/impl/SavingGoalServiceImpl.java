package money.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.saving.SavingGoalDepositRequest;
import money.dto.saving.SavingGoalRequest;
import money.entity.Account;
import money.entity.SavingGoal;
import money.entity.Transaction;
import money.entity.User;
import money.enums.SavingStatus;
import money.enums.TransactionType;
import money.repository.AccountRepository;
import money.repository.SavingGoalRepository;
import money.repository.TransactionRepository;
import money.repository.UserRepository;
import money.service.ISavingGoalService;

@Service
public class SavingGoalServiceImpl implements ISavingGoalService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private SavingGoalRepository savingGoalRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;

	@Override
	public List<SavingGoal> getListSavingGoal(String email) {
		return savingGoalRepo.findByUser_Email(email);
	}

	@Override
	public SavingGoal getSavingGoalById(String email, Long id) {
		SavingGoal goal = savingGoalRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy mục tiêu tiết kiệm"));
		if (!goal.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xem mục tiêu tiết kiệm này");
		}
		return goal;
	}

	@Override
	@Transactional
	public SavingGoal addSavingGoal(String email, SavingGoalRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		SavingGoal goal = new SavingGoal();
		goal.setUser(user);
		goal.setName(request.getName());
		goal.setTargetAmount(request.getTargetAmount());
		goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : 0.0);
		goal.setDeadline(request.getDeadline());
		
		if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
			goal.setStatus(SavingStatus.ACHIEVED);
		} else {
			goal.setStatus(SavingStatus.IN_PROGRESS);
		}
		
		return savingGoalRepo.save(goal);
	}

	@Override
	@Transactional
	public SavingGoal updateSavingGoal(String email, Long id, SavingGoalRequest request) {
		SavingGoal goal = getSavingGoalById(email, id);
		
		goal.setName(request.getName());
		goal.setTargetAmount(request.getTargetAmount());
		goal.setDeadline(request.getDeadline());
		if (request.getCurrentAmount() != null) {
			goal.setCurrentAmount(request.getCurrentAmount());
		}
		
		if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
			goal.setStatus(SavingStatus.ACHIEVED);
		} else {
			goal.setStatus(SavingStatus.IN_PROGRESS);
		}
		
		return savingGoalRepo.save(goal);
	}

	@Override
	@Transactional
	public void deleteSavingGoal(String email, Long id) {
		SavingGoal goal = getSavingGoalById(email, id);
		savingGoalRepo.delete(goal);
	}

	@Override
	@Transactional
	public SavingGoal deposit(String email, Long id, SavingGoalDepositRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		SavingGoal goal = getSavingGoalById(email, id);
		
		Account account = accountRepo.findById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Tài khoản ví không tìm thấy"));
		
		if (!account.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền thực hiện giao dịch từ tài khoản này");
		}
		
		double amount = request.getAmount();
		if (account.getCurrentBalance() < amount) {
			throw new RuntimeException("Số dư tài khoản ví không đủ để thực hiện gửi tiết kiệm");
		}
		
		// Update balance
		account.setCurrentBalance(account.getCurrentBalance() - amount);
		accountRepo.save(account);
		
		// Update saving goal
		goal.setCurrentAmount(goal.getCurrentAmount() + amount);
		if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
			goal.setStatus(SavingStatus.ACHIEVED);
		} else {
			goal.setStatus(SavingStatus.IN_PROGRESS);
		}
		SavingGoal savedGoal = savingGoalRepo.save(goal);
		
		// Save Transaction
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(account);
		trans.setCategory(null);
		trans.setType(TransactionType.EXPENSE);
		trans.setAmount(amount);
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote("Tích lũy cho mục tiêu: " + goal.getName() 
				+ (request.getNote() != null ? " - " + request.getNote() : ""));
		transactionRepo.save(trans);
		
		return savedGoal;
	}

	@Override
	@Transactional
	public SavingGoal withdraw(String email, Long id, SavingGoalDepositRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		SavingGoal goal = getSavingGoalById(email, id);
		
		Account account = accountRepo.findById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Tài khoản ví không tìm thấy"));
		
		if (!account.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền thực hiện giao dịch từ tài khoản này");
		}
		
		double amount = request.getAmount();
		if (goal.getCurrentAmount() < amount) {
			throw new RuntimeException("Số dư tích lũy hiện tại không đủ để rút số tiền này");
		}
		
		// Update saving goal
		goal.setCurrentAmount(goal.getCurrentAmount() - amount);
		if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
			goal.setStatus(SavingStatus.ACHIEVED);
		} else {
			goal.setStatus(SavingStatus.IN_PROGRESS);
		}
		SavingGoal savedGoal = savingGoalRepo.save(goal);
		
		// Update account balance
		account.setCurrentBalance(account.getCurrentBalance() + amount);
		accountRepo.save(account);
		
		// Save Transaction
		Transaction trans = new Transaction();
		trans.setUser(user);
		trans.setAccount(account);
		trans.setCategory(null);
		trans.setType(TransactionType.INCOME);
		trans.setAmount(amount);
		trans.setTransactionDate(LocalDateTime.now());
		trans.setNote("Rút tiết kiệm từ mục tiêu: " + goal.getName() 
				+ (request.getNote() != null ? " - " + request.getNote() : ""));
		transactionRepo.save(trans);
		
		return savedGoal;
	}
}
