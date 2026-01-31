package money.service.impl;

import java.time.LocalDateTime;
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
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCreatedAt(LocalDateTime.now());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword); 

        return userRepo.save(user);
	}

	@Override
	public boolean verifyLogin(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
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

        // OTP hợp lệ → xoá
        user.setOtp(null);
        user.setOtpExpiredAt(null);
        userRepo.save(user);
		
	}

}
