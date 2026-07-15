package money.service;

import java.util.List;

import money.dto.budget.BudgetRequest;
import money.entity.Budget;

public interface IBudgetService {

	List<Budget> getListBudget(String email);
	
	Budget getBudgetById(String email, Long id);
	
	Budget addBudget(String email, BudgetRequest request);
	
	Budget updateBudget(String email, Long id, BudgetRequest request);
	
	void deleteBudget(String email, Long id);
}
