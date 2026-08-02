package money.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import money.service.IAccountService;

@Controller
public class WebViewController {

    @Autowired
    private IAccountService accountService;

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
    public String accounts(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("accountsByType", accountService.getAccountListByAccountType(email));
        return "pages/account";
    }

    @GetMapping("/transaction")
    public String transaction() {
        return "pages/transaction";
    }

    @GetMapping("/wallet")
    public String wallet() {
        return "pages/wallet";
    }

    @GetMapping("/investment")
    public String investment() {
        return "pages/investment";
    }

    @GetMapping("/stocks-fund")
    public String stocksFund() {
        return "pages/stocksFund";
    }

    @GetMapping("/community")
    public String community() {
        return "pages/community";
    }

    @GetMapping("/support")
    public String support() {
        return "pages/support";
    }

    @GetMapping("/documentation")
    public String documentation() {
        return "pages/documentation";
    }

    @GetMapping("/categories")
    public String categories() {
        return "pages/category";
    }

    @GetMapping("/budgets")
    public String budgets() {
        return "pages/budget";
    }

    @GetMapping("/savings")
    public String savings() {
        return "pages/saving";
    }

    @GetMapping("/debts")
    public String debts() {
        return "pages/debt";
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
