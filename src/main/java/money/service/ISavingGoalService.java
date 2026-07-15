package money.service;

import java.util.List;

import money.dto.saving.SavingGoalDepositRequest;
import money.dto.saving.SavingGoalRequest;
import money.entity.SavingGoal;

public interface ISavingGoalService {

	List<SavingGoal> getListSavingGoal(String email);
	
	SavingGoal getSavingGoalById(String email, Long id);
	
	SavingGoal addSavingGoal(String email, SavingGoalRequest request);
	
	SavingGoal updateSavingGoal(String email, Long id, SavingGoalRequest request);
	
	void deleteSavingGoal(String email, Long id);
	
	SavingGoal deposit(String email, Long id, SavingGoalDepositRequest request);
	
	SavingGoal withdraw(String email, Long id, SavingGoalDepositRequest request);
}
