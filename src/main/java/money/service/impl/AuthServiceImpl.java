package money.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import money.dto.auth.JwtResponse;
import money.dto.auth.LoginRequest;
import money.dto.auth.ResetPasswordRequest;
import money.dto.auth.SignupRequest;
import money.entity.User;
import money.repository.UserRepository;
import money.service.IAuthService;
import money.service.IEmailService;
import money.util.JwtUtil;

@Service
public class AuthServiceImpl implements IAuthService{

	@Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private IEmailService emailService;
    
    private String generateOTP() {
		return String.format("%06d", new Random().nextInt(999999));
	}
    
	@Override
	public User register(SignupRequest request) {
		if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new RuntimeException("Confirm password phải giống password");
        } 
		
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đăng ký đã tồn tại!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setVerified(false);
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword); 
        
        User savedUser = userRepo.save(user);
        
        try {
            sendOTP(savedUser.getEmail());
        } catch (Exception e) {
            // Log or wrap the exception
            throw new RuntimeException("Đăng ký thành công nhưng không gửi được mã OTP. Vui lòng yêu cầu gửi lại OTP.", e);
        }

        return savedUser;
	}

	@Override
	public boolean verifyLogin(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        if (!user.isVerified()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt/xác thực OTP. Vui lòng xác thực trước khi đăng nhập.");
        }
        
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
	}
//	
//
//	@Override
//	public boolean verifyOTP(String email, String otp) {
//		User user = userRepo.findByEmail(email)
//				.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
//		if (otp != null && user.getOTP().equals(otp))
//			return true;
//		return false;
//	}

	@Override
	public void resetPassword(ResetPasswordRequest request) {
		
		
		
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        if (passwordEncoder.encode(request.getNewPassword()).equals(user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
	}

	@Override
	public void sendOTP(String email) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

		String otp = generateOTP(); 
        user.setOtp(otp);
        user.setOtpExpiredAt(LocalDateTime.now().plusMinutes(5));

        userRepo.save(user);
        emailService.sendOTP(email, otp);
	}
	

	@Override
	public void verifyOtp(String email, String otp) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (user.getOtp() == null || user.getOtpExpiredAt() == null) {
            throw new RuntimeException("OTP chưa được tạo");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiredAt())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("OTP không chính xác");
        }

        // OTP hợp lệ → xoá và kích hoạt tài khoản
        user.setOtp(null);
        user.setOtpExpiredAt(null);
        user.setVerified(true);
        userRepo.save(user);
		
	}

}
