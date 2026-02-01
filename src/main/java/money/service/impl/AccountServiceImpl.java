package money.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import money.dto.account.AccountRequest;
import money.dto.account.UpdateAccountRequest;
import money.entity.Account;
import money.entity.User;
import money.enums.AccountType;
import money.repository.AccountRepository;
import money.repository.UserRepository;
import money.service.IAccountService;

@Service
public class AccountServiceImpl implements IAccountService{
	
	@Autowired
    private UserRepository userRepo; 
	
	@Autowired
    private AccountRepository accountRepo;

	@Override
	public Map<AccountType, List<Account>> getAccountListByAccountType(String email) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
		List<Account> accounts = accountRepo.findByUser_Email(email);
		return accounts.stream()
		        .collect(Collectors.groupingBy(
		                Account::getAccountType,
		                () -> new EnumMap<>(AccountType.class),
		                Collectors.toList()
		        ));

	}

	@Override
	public Account addAccount(String email, AccountRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
		
		Account acc = new Account();
		acc.setUser(user);
		acc.setAccountName(request.getAccountName());
		acc.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()));
		acc.setInitialBalance(request.getInitialBalance());
		acc.setCurrentBalance(request.getInitialBalance());
		acc.setCurrency(request.getCurrency());
		
		return accountRepo.save(acc);
	}

	@Override
	public Account editAccount(String email, Long accountId, UpdateAccountRequest request) {
		Account acc = accountRepo.findById(accountId)
              .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

      if (!acc.getUser().getEmail().equals(email)) {
          throw new RuntimeException("Bạn không có quyền sửa tài khoản này!");
      }

      acc.setAccountName(request.getAccountName());
      acc.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()));
      return accountRepo.save(acc);
	}

	@Override
	public void deleteAccount(String email, Long id) {
		Account acc = accountRepo.findById(id)
	              .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
		
		if (!acc.getUser().getEmail().equals(email)) {
	          throw new RuntimeException("Bạn không có quyền sửa tài khoản này!");
	      }
	    accountRepo.delete(acc);
	}

}
