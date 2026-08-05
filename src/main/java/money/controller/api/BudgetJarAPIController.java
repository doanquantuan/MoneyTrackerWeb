package money.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import money.dto.jar.BudgetJarPercentageRequest;
import money.dto.jar.BudgetJarTransferRequest;
import money.service.IBudgetJarService;

@RestController
@RequestMapping("/api/jars")
public class BudgetJarAPIController {

	@Autowired
	private IBudgetJarService budgetJarService;

	private String getCurrentUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@GetMapping
	public ResponseEntity<?> getJars() {
		try {
			String email = getCurrentUserEmail();
			return ResponseEntity.ok(budgetJarService.getJarsByUser(email));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/percentages")
	public ResponseEntity<?> updatePercentages(@RequestBody List<BudgetJarPercentageRequest> request) {
		try {
			String email = getCurrentUserEmail();
			return ResponseEntity.ok(budgetJarService.updateJarPercentages(email, request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> transferMoney(@RequestBody BudgetJarTransferRequest request) {
		try {
			String email = getCurrentUserEmail();
			budgetJarService.transferBetweenJars(email, request);
			return ResponseEntity.ok("Chuyển tiền thành công");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
