package money.service;

import money.dto.transaction.TransactionRequest;
import money.entity.Transaction;

public interface ITransactionService {

	Transaction createStandardTransaction(String email, TransactionRequest request);
	
	Transaction transferMoney();
	
	Transaction recordDebtTransaction();
	
	void getTransactionById();
	
	void updateTransaction();
	
	void deleteTransaction();
	
	void searchTransactions();
}
