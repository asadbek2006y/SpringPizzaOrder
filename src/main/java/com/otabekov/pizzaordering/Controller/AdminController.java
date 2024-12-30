package com.otabekov.pizzaordering.Controller;

import com.otabekov.pizzaordering.Entity.Pizza;
import com.otabekov.pizzaordering.Repositories.PizzaRepository;
import com.otabekov.pizzaordering.Repositories.UserRepository;
import com.otabekov.pizzaordering.Service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private PizzaRepository pizzaRepository;

    // Dashboard page
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        long userCount = userRepository.count();
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userCount", userCount);
        return "admin/dashboard";
    }

    // Users management page
    @GetMapping("/users")
    public String getUsers(Model model) {
        long userCount = userRepository.count();
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userCount", userCount);
        return "admin/users";
    }

    // Products management page
    @GetMapping("/products")
    public String getAllPizzas(Model model) {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        model.addAttribute("pizzas", pizzas);
        return "admin/products";
    }

    // Add pizza form page
    @GetMapping("/products/add")
    public String getAddPizzaForm(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "admin/addPizza";
    }

    // Add new pizza
    @PostMapping("/products/add/pizza")
    public String addPizza(@ModelAttribute Pizza pizza,
                           @RequestParam("image") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique file name
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Save the image file
            Files.write(filePath, imageFile.getBytes());

            // Save image path and pizza data to the database
            pizza.setPizzaImage("/uploads/" + fileName);
            pizzaRepository.save(pizza);

            redirectAttributes.addFlashAttribute("message", "Pizza added successfully!");
            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error uploading image!");
            return "redirect:/admin/products/add";
        }
    }
    @PostMapping("/delete/{id}")
    public String deletePizza(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Check if the pizza exists
            Pizza pizza = pizzaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pizza not found with id: " + id));

            // Delete the pizza
            pizzaRepository.delete(pizza);

            // Remove the uploaded image file
            Path imagePath = Paths.get("uploads", pizza.getPizzaImage());
            Files.deleteIfExists(imagePath);

            redirectAttributes.addFlashAttribute("message", "Pizza deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting pizza: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

}
