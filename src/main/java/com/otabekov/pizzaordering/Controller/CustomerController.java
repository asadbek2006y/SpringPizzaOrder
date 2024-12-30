package com.otabekov.pizzaordering.Controller;

import com.otabekov.pizzaordering.Entity.Order;
import com.otabekov.pizzaordering.Entity.Pizza;
import com.otabekov.pizzaordering.Entity.User;
import com.otabekov.pizzaordering.Repositories.OrderRepository;
import com.otabekov.pizzaordering.Repositories.PizzaRepository;
import com.otabekov.pizzaordering.Repositories.UserRepository;
import com.otabekov.pizzaordering.Service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private PizzaService pizzaService;
    @Autowired
    private PizzaRepository pizzaRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String customerDashboard(Model model) {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        model.addAttribute("pizzas", pizzas);
        return "customer/dashboard"; // `dashboard.html` sahifasini render qiladi
    }
    @GetMapping("/my-orders")
    public String getUserOrders(Model model) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error";
        }

        // Get orders by user ID
        List<Order> orders = orderRepository.findByUserId(user.getId());
        model.addAttribute("orders", orders);

        return "customer/my-orders"; // A Thymeleaf template to display orders
    }
}
