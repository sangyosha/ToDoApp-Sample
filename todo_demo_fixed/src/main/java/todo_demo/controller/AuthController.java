package todo_demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import todo_demo.service.UserService;

@Controller
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }
    
    @GetMapping("/")
    public String root() {
        return "redirect:/todos";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            service.register(username, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // ユニーク制約違反の場合
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "ユーザー登録に失敗しました");
            return "register";
        }
    }
}
