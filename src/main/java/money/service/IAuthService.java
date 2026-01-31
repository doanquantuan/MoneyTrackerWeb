package money.service;

import money.dto.auth.JwtResponse;
import money.dto.auth.LoginRequest;
import money.dto.auth.ResetPasswordRequest;
import money.dto.auth.SignupRequest;
import money.entity.User;

public interface IAuthService {
	
	public User register(SignupRequest request);
	
//	public JwtResponse login(LoginRequest request)
	
	public void sendOTP(String email);
	
	public void verifyOtp(String email, String otp);
	
	public boolean verifyLogin(LoginRequest request);
//	
//	public boolean verifyOTP(String email, String otp);

	public void resetPassword(ResetPasswordRequest request);
}
