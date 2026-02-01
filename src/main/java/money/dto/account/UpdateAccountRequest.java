package money.dto.account;

import lombok.Data;

@Data
public class UpdateAccountRequest {
	
	private String accountName;
	
	private String accountType;
	
}
