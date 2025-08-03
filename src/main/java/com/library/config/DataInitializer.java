package com.library.config;

import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            userService.createDefaultAdmin();
            System.out.println("=".repeat(50));
            System.out.println("LIBRARY MANAGEMENT SYSTEM INITIALIZED");
            System.out.println("=".repeat(50));
            System.out.println("Default admin user created:");
            System.out.println("   Username: admin2");
            System.out.println("   Password: 123456");
            System.out.println("=".repeat(50));
            System.out.println("Access the application at: http://localhost:8080");
            System.out.println("=".repeat(50));
        }
    }
}
