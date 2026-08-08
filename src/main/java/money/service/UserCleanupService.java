package money.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import money.entity.User;
import money.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

	private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void deleteUnverifiedUsers() {

        LocalDateTime limit =
                LocalDateTime.now().minusHours(1);

        List<User> users =
                userRepository.findByIsVerifiedFalseAndCreatedAtBefore(limit);

        if (!users.isEmpty()) {
            userRepository.deleteAll(users);
        }
    }
}
