package money.service;

import java.util.List;

import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Transaction;

public interface ITransactionService {

	Transaction createStandardTransaction(String email, TransactionInExRequest request);
	
	Transaction transferMoney(String email, TransferRequest request);
	
	Transaction recordDebtTransaction();
	
	List<Transaction> getListRecentTransaction(String email);
	
	void getTransactionById();
	
	void updateTransaction();
	
	void deleteTransaction();
	
	void searchTransactions();
}
