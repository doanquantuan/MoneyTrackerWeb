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
import money.dto.account.AccountRequest;
import money.dto.account.UpdateAccountRequest;
import money.dto.auth.ApiResponse;
import money.entity.Account;
import money.enums.AccountType;
import money.service.IAccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountAPIController {
	
	@Autowired
    private IAccountService accountService;

	@GetMapping
	public ResponseEntity<ApiResponse<Map<AccountType, List<Account>>>> getAccounts(){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ApiResponse<Map<AccountType, List<Account>>> response = new ApiResponse<>();
		response.setSuccess(true);
		response.setMessage("Lấy danh sách tài khoản thành công");
		response.setData(accountService.getAccountListByAccountType(email));
		response.setError(null);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<Account>> addAccount(@Valid @RequestBody AccountRequest request){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 

		try {
			Account account = accountService.addAccount(email, request);
			
			ApiResponse<Account> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("Thêm tài khoản thành công");
			response.setData(account);
			response.setError(null);
			
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Account> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);
			
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Account>> editAccount(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequest request){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 

		try {
			Account account = accountService.editAccount(email, id, request);
			
			ApiResponse<Account> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("Cập nhật tài khoản thành công");
			response.setData(account);
			response.setError(null);
			
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Account> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);
			
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 
		
		try {
			accountService.deleteAccount(email, id);
			
			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("Xóa tài khoản thành công");
			response.setData(null);
			response.setError(null);
			
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);
			
			return ResponseEntity.badRequest().body(response);
		}
	}
}
