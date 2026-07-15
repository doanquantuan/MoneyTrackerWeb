package money.service;

import java.util.List;

import money.dto.debt.DebtRepaymentRequest;
import money.dto.debt.DebtRequest;
import money.entity.Debt;
import money.entity.DebtRepayment;

public interface IDebtService {

	List<Debt> getListDebt(String email);
	
	Debt getDebtById(String email, Long id);

	Debt addDebt(String email, DebtRequest request);
	
	DebtRepayment repayDebt(String email, Long debtId, DebtRepaymentRequest request);
	
	void deleteDebt(String email, Long id);
}
