package money.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.transaction.TransactionRequest;
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
	public Transaction createStandardTransaction(String email, TransactionRequest request) {
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
		
		return transactionRepo.save(trans);
	}

	@Override
	public Transaction transferMoney() {
		// TODO Auto-generated method stub
		return null;
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
	public void updateTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void searchTransactions() {
		// TODO Auto-generated method stub
		
	}

}
