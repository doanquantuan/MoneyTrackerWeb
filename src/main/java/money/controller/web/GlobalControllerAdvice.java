package money.controller.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import money.entity.User;
import money.repository.UserRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("fullName")
    public String getFullName() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String email = auth.getName();
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    String first = user.getFirstName() != null ? user.getFirstName() : "";
                    String last = user.getLastName() != null ? user.getLastName() : "";
                    String full = (first + " " + last).trim();
                    return full.isEmpty() ? email : full;
                }
            }
        } catch (Exception e) {
            // Safe fallback
        }
        return "Người dùng";
    }
}
