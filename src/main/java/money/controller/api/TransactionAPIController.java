package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import money.dto.transaction.TransactionInExRequest;
import money.dto.transaction.TransactionRequest;
import money.dto.transaction.TransferRequest;
import money.entity.Transaction;
import money.service.ITransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionAPIController {
	
	@Autowired
	private ITransactionService transactionService;
	
	private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

	@PostMapping
	public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
		try {
			String email = getCurrentUserEmail();
			
			Transaction transaction = new Transaction();
	
			if (request.getType() == "INCOME" || request.getType() == "EXPENSE") {
				TransactionInExRequest req = null;
				req.setAccountId(request.getAccountId());
				req.setAmount(request.getAmount());
				req.setCategoryId(request.getCategoryId());
				req.setNote(request.getNote());
				req.setType(request.getType());
				
				transaction = transactionService.createStandardTransaction(email, req);
				return ResponseEntity.ok(transaction);
			} else if (request.getType() == "TRANSFER") {
				TransferRequest req = null;
				req.setAccountId(request.getAccountId());
				req.setToAccountId(request.getToAccountId());
				req.setAmount(request.getAmount());
				req.setNote(request.getNote());
				req.setType(request.getType());
				
				transaction = transactionService.transferMoney(email, req);
				return ResponseEntity.ok(transaction);
			} 
		    return ResponseEntity.ok(transaction);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
