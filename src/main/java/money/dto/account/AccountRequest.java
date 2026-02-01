package money.dto.account;

import lombok.Data;

@Data
public class AccountRequest {

	private String accountName;
	
	private String accountType;
	
	private Double initialBalance;
	
	private Double currentBalance;
	
	private String currency;
	

}
