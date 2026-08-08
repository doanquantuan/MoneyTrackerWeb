package money.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import money.dto.auth.ApiResponse;
import money.dto.auth.ForgetPasswordRequest;
import money.dto.auth.JwtResponse;
import money.dto.auth.LoginRequest;
import money.dto.auth.ResetPasswordRequest;
import money.dto.auth.SignupRequest;
import money.dto.auth.VerifyOtpRequest;
import money.repository.UserRepository;
import money.service.IAuthService;
import money.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthAPIController {

	@Autowired
	private IAuthService authService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepo;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(
			@Valid @RequestBody SignupRequest request) {

		try {
			authService.register(request);

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("OTP xác thực đã được gửi về email của bạn");
			response.setData(null);
			response.setError(null);

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			boolean isValid = authService.verifyLogin(request);

			if (isValid) {
				String token = jwtUtil.generateToken(request.getEmail());

				return ResponseEntity.ok(new JwtResponse(token, request.getEmail()));
			} else {
				return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu");
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

	@PostMapping("/forget-password")
	public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest request) {

		try {
			authService.sendOTP(request.getEmail());

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("OTP đã được gửi lại email của bạn");
			response.setData(null);
			response.setError(null);

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

		try {
			authService.verifyOtp(request.getEmail(), request.getOtp());

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("OTP xác thực thành công");
			response.setData(null);
			response.setError(null);

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<Void>> resendOtp(@Valid @RequestBody Map<String, String> email) {

		try {
			authService.sendOTP(email.get("email"));

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("OTP xác thực đã được gửi lại email của bạn");
			response.setData(null);
			response.setError(null);

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

		try {
			authService.resetPassword(request);

			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(true);
			response.setMessage("Password đã được thay đổi");
			response.setData(null);
			response.setError(null);

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			ApiResponse<Void> response = new ApiResponse<>();
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setData(null);
			response.setError(null);

			return ResponseEntity.badRequest().body(response);
		}
	}
}
