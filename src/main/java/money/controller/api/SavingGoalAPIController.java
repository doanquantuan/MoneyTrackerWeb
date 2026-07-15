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
import money.dto.saving.SavingGoalDepositRequest;
import money.dto.saving.SavingGoalRequest;
import money.entity.SavingGoal;
import money.service.ISavingGoalService;

@RestController
@RequestMapping("/api/savings")
public class SavingGoalAPIController {

	@Autowired
	private ISavingGoalService savingGoalService;

	private String getCurrentUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@GetMapping
	public ResponseEntity<?> getSavingGoals() {
		try {
			String email = getCurrentUserEmail();
			return ResponseEntity.ok(savingGoalService.getListSavingGoal(email));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getSavingGoal(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			SavingGoal goal = savingGoalService.getSavingGoalById(email, id);
			return ResponseEntity.ok(goal);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> addSavingGoal(@Valid @RequestBody SavingGoalRequest request) {
		try {
			String email = getCurrentUserEmail();
			SavingGoal goal = savingGoalService.addSavingGoal(email, request);
			return ResponseEntity.ok(goal);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateSavingGoal(@PathVariable Long id, @Valid @RequestBody SavingGoalRequest request) {
		try {
			String email = getCurrentUserEmail();
			SavingGoal goal = savingGoalService.updateSavingGoal(email, id, request);
			return ResponseEntity.ok(goal);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteSavingGoal(@PathVariable Long id) {
		try {
			String email = getCurrentUserEmail();
			savingGoalService.deleteSavingGoal(email, id);
			return ResponseEntity.ok("Xóa mục tiêu tiết kiệm thành công");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/{id}/deposit")
	public ResponseEntity<?> deposit(@PathVariable Long id, @Valid @RequestBody SavingGoalDepositRequest request) {
		try {
			String email = getCurrentUserEmail();
			SavingGoal goal = savingGoalService.deposit(email, id, request);
			return ResponseEntity.ok(goal);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/{id}/withdraw")
	public ResponseEntity<?> withdraw(@PathVariable Long id, @Valid @RequestBody SavingGoalDepositRequest request) {
		try {
			String email = getCurrentUserEmail();
			SavingGoal goal = savingGoalService.withdraw(email, id, request);
			return ResponseEntity.ok(goal);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
