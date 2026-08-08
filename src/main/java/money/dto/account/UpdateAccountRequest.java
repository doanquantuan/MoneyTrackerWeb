package money.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountRequest {
	
	@NotBlank(message = "Tên tài khoản không được để trống")
	@Size(max = 50, message = "Tên tài khoản không được vượt quá 50 ký tự")
	private String accountName;
	
	@NotBlank(message = "Loại tài khoản không được để trống")
	@Pattern(regexp = "CASH|BANK|DEBIT|CREDIT|EWALLET|SAVINGS|INVESTMENT|LOAN|cash|bank|debit|credit|ewallet|savings|investment|loan", message = "Loại tài khoản không hợp lệ")
	private String accountType;
	
}
