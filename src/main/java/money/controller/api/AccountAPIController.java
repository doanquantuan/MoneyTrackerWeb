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

import money.dto.account.AccountRequest;
import money.dto.account.UpdateAccountRequest;
import money.entity.Account;
import money.service.IAccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountAPIController {
	
	@Autowired
    private IAccountService accountService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

	@GetMapping
	public ResponseEntity<?> getAccounts(){
		String email = getCurrentUserEmail();
        return ResponseEntity.ok(accountService.getAccountListByAccountType(email));
	}
	
	@PostMapping
	public ResponseEntity<?> addAccount(@RequestBody AccountRequest request){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 

	    Account account = accountService.addAccount(email, request);
	    return ResponseEntity.ok(account);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editAccount(@PathVariable Long id, @RequestBody UpdateAccountRequest request){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 

	    Account account = accountService.editAccount(email, id, request);
	    return ResponseEntity.ok(account);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable Long id){
		String email = SecurityContextHolder.getContext().getAuthentication().getName(); 
		accountService.deleteAccount(email, id);

	    return ResponseEntity.ok("Xóa tài khoản thành công");
	}
}
