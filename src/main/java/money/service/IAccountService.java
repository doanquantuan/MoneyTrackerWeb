package money.service;

import java.util.List;
import java.util.Map;

import money.dto.account.AccountRequest;
import money.dto.account.UpdateAccountRequest;
import money.entity.Account;
import money.enums.AccountType;

public interface IAccountService {

	Map<AccountType, List<Account>> getAccountListByAccountType(String email);
	
	Account addAccount(String email, AccountRequest request);
	
	Account editAccount(String email, Long id, UpdateAccountRequest request);
	
	void deleteAccount(String email, Long id);
}
