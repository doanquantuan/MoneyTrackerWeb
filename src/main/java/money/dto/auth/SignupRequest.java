package money.dto.auth;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password; 
    private String confirmPassword;
    private String firstName;
    private String lastName;
}