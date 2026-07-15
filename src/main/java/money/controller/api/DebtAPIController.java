package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import money.dto.debt.DebtRepaymentRequest;
import money.dto.debt.DebtRequest;
import money.entity.Debt;
import money.entity.DebtRepayment;
import money.service.IDebtService;

@RestController
@RequestMapping("/api/debts")
public class DebtAPIController {
	
	@Autowired
	private IDebtService debtService;
	
	private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
	
	@GetMapping
	public ResponseEntity<?> getDebts() {
		try {
			String email = getCurrentUserEmail();
			return ResponseEntity.ok(debtService.getListDebt(email));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getDebt(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			Debt debt = debtService.getDebtById(email, id);
			return ResponseEntity.ok(debt);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
    public ResponseEntity<?> addDebt(@Valid @RequestBody DebtRequest request) {
		try {
			String email = getCurrentUserEmail();
		    Debt debt = debtService.addDebt(email, request);
		    return ResponseEntity.ok(debt);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }
	
	@PostMapping("/{id}/repayments")
	public ResponseEntity<?> repayDebt(@PathVariable Long id, @Valid @RequestBody DebtRepaymentRequest request) {
		try {
			String email = getCurrentUserEmail();
			DebtRepayment repayment = debtService.repayDebt(email, id, request);
			return ResponseEntity.ok(repayment);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDebt(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			debtService.deleteDebt(email, id);
			return ResponseEntity.ok("Xóa khoản nợ thành công");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
