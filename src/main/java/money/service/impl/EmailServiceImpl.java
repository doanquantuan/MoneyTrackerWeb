package money.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import money.entity.User;
import money.repository.UserRepository;
import money.service.IEmailService;

;

@Service
public class EmailServiceImpl implements IEmailService{
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserRepository userRepo;
	
	

	@Override
	public void sendOTP(String toEmail, String otp) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setSubject("Your Money Tracker OTP Code");
            message.setText(
                "Hello,\n\n" +
                "Your One-Time Password (OTP) for Money Tracker is:\n\n" +
                otp + "\n\n" +
                "This code is valid for 5 minutes.\n" +
                "Please do not share this code with anyone.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Money Tracker Security Team"
            );
            message.setFrom("td735429@gmail.com");
            mailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException("Không thể gửi email OTP", e);
		}
	}
	
	

}
