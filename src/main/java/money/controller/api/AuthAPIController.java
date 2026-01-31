package money.controller.api;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import money.dto.auth.ForgetPasswordRequest;
import money.dto.auth.JwtResponse;
import money.dto.auth.LoginRequest;
import money.dto.auth.ResetPasswordRequest;
import money.dto.auth.SignupRequest;
import money.dto.auth.VerifyOtpRequest;
import money.entity.User;
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
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean isValid = authService.verifyLogin(request); 
        
        if (isValid) {
            String token = jwtUtil.generateToken(request.getEmail());

            return ResponseEntity.ok(new JwtResponse(token, request.getEmail()));
        } else {
            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu");
        }
    }
    
    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest request) {
    	authService.sendOTP(request.getEmail());

        return ResponseEntity.ok(
            Map.of("message", "Nếu email tồn tại, mã OTP đã được gửi")
        );
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {

        authService.verifyOtp(request.getEmail(), request.getOtp());

        return ResponseEntity.ok(
            Map.of("message", "OTP hợp lệ")
        );
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
            Map.of("message", "Đặt lại mật khẩu thành công")
        );
    }
}
