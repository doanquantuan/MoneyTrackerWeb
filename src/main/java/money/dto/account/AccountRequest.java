package money.dto.account;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRequest {

	@NotBlank(message = "Tên tài khoản không được để trống")
	@Size(max = 50, message = "Tên tài khoản không được vượt quá 50 ký tự")
	private String accountName;

	@NotBlank(message = "Loại tài khoản không được để trống")
	@Pattern(regexp = "CASH|BANK|DEBIT|CREDIT|EWALLET|SAVINGS|INVESTMENT|LOAN|cash|bank|debit|credit|ewallet|savings|investment|loan", message = "Loại tài khoản không hợp lệ")
	private String accountType;

	@NotNull(message = "Số dư ban đầu không được để trống")
	@Min(value = 0, message = "Số dư ban đầu phải lớn hơn hoặc bằng 0")
	private Double initialBalance;

	private Double currentBalance;

	@NotBlank(message = "Đơn vị tiền tệ không được để trống")
	@Size(max = 10, message = "Đơn vị tiền tệ không được vượt quá 10 ký tự")
	private String currency;

}
