package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailService emailService;  // New service to send emails

    // Temporary store for OTPs (use a cache like Redis for better performance)
    private final Map<String, String> otpStorage = new HashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // Generate a 6-digit OTP
    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // Step 1: Request OTP for password reset
    public String requestPasswordReset(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "User not found";
        }

        String otp = generateOTP();
        otpStorage.put(email, otp);  // Store OTP temporarily

        // Send OTP via email
        emailService.sendEmail(email, "Password Reset OTP", "Your OTP is: " + otp);

        return "OTP sent successfully";
    }

    // Step 2: Verify OTP
    public boolean verifyOTP(String email, String otp) {
        return otpStorage.containsKey(email) && otpStorage.get(email).equals(otp);
    }

    // Step 3: Reset Password
    public String resetPassword(String email, String otp, String newPassword) {
        if (!verifyOTP(email, otp)) {
            return "Invalid or expired OTP";
        }

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            otpStorage.remove(email);  // Clear OTP after successful reset
            return "Password reset successful";
        }
        return "User not found";
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public RegisterResponseDTO register(RegisterRequestDTO req) {
        // Check if email is already registered
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return new RegisterResponseDTO(null, "Email is already registered. Please use a different email.");
        }
        if (userRepository.findByUsername(req.getUsername()).isPresent()){
            return new RegisterResponseDTO(null,"Username is already registered. Please use a different username");
        }

        try {
            UserEntity newUser = new UserEntity(
                    req.getFirstname(),
                    req.getLastname(),
                    req.getEmail(),
                    req.getUsername(),
                    req.getRole(),
                    passwordEncoder.encode(req.getPassword()) // Encrypt password
            );

            UserEntity savedUser = userRepository.save(newUser);

            return new RegisterResponseDTO("User registered successfully with ID: " + savedUser.getId(), null);
        } catch (DataIntegrityViolationException e) {
            return new RegisterResponseDTO(null, "Registration failed due to duplicate data.");
        } catch (Exception e) {
            return new RegisterResponseDTO(null, "System error occurred: " + e.getMessage());
        }
    }

    public LoginResponseDTO login(LoginRequestDTO loginData) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isEmpty()) {
            return new LoginResponseDTO(null, null, "User not found", "error");
        }

        UserEntity user = userOptional.get();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), loginData.getPassword())
            );
        } catch (Exception e) {
            return new LoginResponseDTO(null, null, "Invalid credentials", "error");
        }

        // Generate JWT token with user role
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String token = jwtService.getJWTToken(user.getEmail(), claims);
        return new LoginResponseDTO(token, LocalDateTime.now(), null, "Token generated successfully");
    }
}
