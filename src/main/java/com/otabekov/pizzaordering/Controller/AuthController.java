package com.otabekov.pizzaordering.Controller;

import com.otabekov.pizzaordering.Entity.User;
import com.otabekov.pizzaordering.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // `register.html` sahifasini render qiladi
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }

        userRepository.save(user);
        model.addAttribute("success", "User registered successfully!");
        return "login";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // `login.html` sahifasini render qiladi
    }
}
