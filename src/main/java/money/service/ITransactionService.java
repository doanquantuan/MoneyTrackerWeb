package money.service;

import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Transaction;

public interface ITransactionService {

	Transaction createStandardTransaction(String email, TransactionInExRequest request);
	
	Transaction transferMoney(String email, TransferRequest request);
	
	Transaction recordDebtTransaction();
	
	void getTransactionById();
	
	void updateTransaction();
	
	void deleteTransaction();
	
	void searchTransactions();
}
