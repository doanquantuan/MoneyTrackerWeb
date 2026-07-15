package money.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.budget.BudgetRequest;
import money.entity.Budget;
import money.entity.Category;
import money.entity.Transaction;
import money.entity.User;
import money.enums.Period;
import money.enums.TransactionType;
import money.repository.BudgetRepository;
import money.repository.CategoryRepository;
import money.repository.TransactionRepository;
import money.repository.UserRepository;
import money.service.IBudgetService;

@Service
public class BudgetServiceImpl implements IBudgetService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BudgetRepository budgetRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;

	@Override
	public List<Budget> getListBudget(String email) {
		return budgetRepo.findByUser_Email(email);
	}

	@Override
	public Budget getBudgetById(String email, Long id) {
		Budget budget = budgetRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy ngân sách"));
		if (!budget.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xem ngân sách này");
		}
		return budget;
	}

	@Override
	@Transactional
	public Budget addBudget(String email, BudgetRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Category category = null;
		if (request.getCategoryId() != null) {
			category = categoryRepo.findById(request.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
		}
		
		Budget budget = new Budget();
		budget.setUser(user);
		budget.setCategory(category);
		budget.setBudgetName(request.getBudgetName());
		budget.setAmountLimit(request.getAmountLimit());
		budget.setPeriod(Period.valueOf(request.getPeriod().toUpperCase()));
		budget.setStartDate(request.getStartDate());
		budget.setEndDate(request.getEndDate());
		
		// Calculate current spending based on existing transactions in range
		double currentSpending = calculateCurrentSpending(user, category, request.getStartDate(), request.getEndDate());
		budget.setCurrentSpending(currentSpending);
		
		return budgetRepo.save(budget);
	}

	@Override
	@Transactional
	public Budget updateBudget(String email, Long id, BudgetRequest request) {
		Budget budget = getBudgetById(email, id);
		
		Category category = null;
		if (request.getCategoryId() != null) {
			category = categoryRepo.findById(request.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
		}
		
		budget.setCategory(category);
		budget.setBudgetName(request.getBudgetName());
		budget.setAmountLimit(request.getAmountLimit());
		budget.setPeriod(Period.valueOf(request.getPeriod().toUpperCase()));
		budget.setStartDate(request.getStartDate());
		budget.setEndDate(request.getEndDate());
		
		// Recalculate current spending
		double currentSpending = calculateCurrentSpending(budget.getUser(), category, request.getStartDate(), request.getEndDate());
		budget.setCurrentSpending(currentSpending);
		
		return budgetRepo.save(budget);
	}

	@Override
	@Transactional
	public void deleteBudget(String email, Long id) {
		Budget budget = getBudgetById(email, id);
		budgetRepo.delete(budget);
	}
	
	private double calculateCurrentSpending(User user, Category category, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
		
		List<Transaction> transactions = transactionRepo.findByUserOrderByTransactionDateDesc(user);
		return transactions.stream()
				.filter(t -> t.getType() == TransactionType.EXPENSE)
				.filter(t -> !t.getTransactionDate().isBefore(startDateTime) && !t.getTransactionDate().isAfter(endDateTime))
				.filter(t -> category == null || (t.getCategory() != null && t.getCategory().getCategoryId() == category.getCategoryId()))
				.mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
				.sum();
	}
}
