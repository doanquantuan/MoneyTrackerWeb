package money.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
	
	@NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
	
	@NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "Password phải có ít nhất 8 ký tự")
    private String password; 
	
	@NotBlank(message = "Confirm password không được để trống")
    @Size(min = 8, message = "Confirm password phải có ít nhất 8 ký tự")
    private String confirmPassword;
	
	@NotBlank(message = "First name không được để trống")
    private String firstName;
	
	@NotBlank(message = "Last name không được để trống")
    private String lastName;
}