package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import money.dto.transaction.TransactionRequest;

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
	
		    Transaction transaction = transactionService.createStandardTransaction(email, request);
		    return ResponseEntity.ok(transaction);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
