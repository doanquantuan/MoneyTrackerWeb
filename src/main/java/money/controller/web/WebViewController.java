package money.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class WebViewController {

    @GetMapping("/login")
    public String login() {
        return "authentication/signIn";
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/signUp";
    }

    @GetMapping("/forgetPassword")
    public String forgetPassword() {
        return "authentication/forgetPassword";
    }

    @GetMapping("/verifyOTP")
    public String verifyOTP() {
        return "authentication/verifyOTP";
    }

    @GetMapping("/resetPassword")
    public String resetPassword() {
        return "authentication/resetPassword";
    }

    @GetMapping({"/", "/index", "/dashboard"})
    public String index() {
        return "index";
    }

    @GetMapping("/accounts")
    public String accounts() {
        return "pages/account";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
