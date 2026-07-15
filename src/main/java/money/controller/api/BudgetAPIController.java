package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import money.dto.budget.BudgetRequest;
import money.entity.Budget;
import money.service.IBudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetAPIController {

	@Autowired
	private IBudgetService budgetService;

	private String getCurrentUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@GetMapping
	public ResponseEntity<?> getBudgets() {
		try {
			String email = getCurrentUserEmail();
			return ResponseEntity.ok(budgetService.getListBudget(email));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getBudget(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			Budget budget = budgetService.getBudgetById(email, id);
			return ResponseEntity.ok(budget);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> addBudget(@Valid @RequestBody BudgetRequest request) {
		try {
			String email = getCurrentUserEmail();
			Budget budget = budgetService.addBudget(email, request);
			return ResponseEntity.ok(budget);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
		try {
			String email = getCurrentUserEmail();
			Budget budget = budgetService.updateBudget(email, id, request);
			return ResponseEntity.ok(budget);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			budgetService.deleteBudget(email, id);
			return ResponseEntity.ok("Xóa ngân sách thành công");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
