package money.service;

import java.util.List;

import money.dto.debt.DebtPeriod;
import money.dto.debt.DebtRepaymentRequest;
import money.dto.debt.DebtRequest;
import money.entity.Debt;
import money.entity.DebtRepayment;

public interface IDebtService {

	List<Debt> getListDebt(String email);
	
	List<DebtRepayment> getListDebtRepayment(String email, Long debtId);
	
	Debt getDebtById(String email, Long id);

	Debt addDebt(String email, DebtRequest request);
	
	DebtRepayment repayDebt(String email, Long debtId, DebtRepaymentRequest request);
	
	void deleteDebt(String email, Long id);
	
	List<DebtPeriod> calculateDebt(String email, Long debtId);
}
