package money.service;

import java.time.LocalDateTime;
import java.util.List;

import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Transaction;

public interface ITransactionService {

	Transaction createStandardTransaction(String email, TransactionInExRequest request);
	
	Transaction transferMoney(String email, TransferRequest request);
	
	Transaction recordDebtTransaction();
	
	List<Transaction> getListRecentTransaction(String email);
	
	Transaction getTransactionById(String email, long id);
	
	Transaction updateTransaction(String email, long id, TransactionInExRequest request);
	
	Transaction updateTransfer(String email, long id, TransferRequest request);
	
	void deleteTransaction(String email, long id);
	
	List<Transaction> searchTransactions(
		String email, Long accountId, Long categoryId, String type, 
		LocalDateTime startDate, LocalDateTime endDate
	);
}
