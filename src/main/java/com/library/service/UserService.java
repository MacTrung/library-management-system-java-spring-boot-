package com.library.service;

import com.library.entity.User;
import com.library.entity.UserRole;
import com.library.entity.UserStatus;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Page<User> findUsers(String keyword, UserStatus status, Pageable pageable) {
        return userRepository.findByKeywordAndStatus(keyword, status, pageable);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(getCurrentUsername());
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        user.setUpdatedBy(getCurrentUsername());
        return userRepository.save(user);
    }
    
    public User updateUserProfile(User user) {
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu không nhập password mới → giữ lại password cũ
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthYear(user.getBirthYear());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setUpdatedBy(getCurrentUsername());
        
        return userRepository.save(existingUser);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? 
                      UserStatus.INACTIVE : UserStatus.ACTIVE);
        user.setUpdatedBy(getCurrentUsername());
        userRepository.save(user);
    }
    
    public void handleFailedLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockoutTime(LocalDateTime.now().plusMinutes(1));
            }
            
            userRepository.save(user);
        }
    }
    
    public void handleSuccessfulLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(0);
            user.setLockoutTime(null);
            userRepository.save(user);
        }
    }
    
    public User createDefaultAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setEmail("admin@library.com");
        admin.setCreatedBy("system");
        return userRepository.save(admin);
    }
    
    public long countActiveUsers() {
        return userRepository.findByKeywordAndStatus("", UserStatus.ACTIVE, Pageable.unpaged()).getTotalElements();
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
