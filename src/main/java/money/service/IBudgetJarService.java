package money.service;

import java.util.List;

import money.dto.jar.BudgetJarPercentageRequest;
import money.dto.jar.BudgetJarTransferRequest;
import money.entity.BudgetJar;
import money.entity.User;

public interface IBudgetJarService {
	List<BudgetJar> getJarsByUser(String email);
	void initializeDefaultJars(User user);
	List<BudgetJar> updateJarPercentages(String email, List<BudgetJarPercentageRequest> requests);
	void transferBetweenJars(String email, BudgetJarTransferRequest request);
}
