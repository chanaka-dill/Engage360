package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping
    public List<UserEntity> getAllUsers(){
        return authService.getAllUsers();
    }

    // Step 1: Request OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestOtp(@RequestBody PasswordResetRequestDTO request) {
        String response = authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO request) {
        boolean isValid = authService.verifyOTP(request.getEmail(), request.getOtp());
        return isValid ? ResponseEntity.ok("OTP verified successfully")
                : ResponseEntity.badRequest().body("Invalid OTP");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO request) {
        String response = authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return response.equals("Password reset successful") ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginData) {
        LoginResponseDTO res = authService.login(loginData);
        if (res.getError() != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO req) {
        RegisterResponseDTO res = authService.register(req);
        if (res.getError() != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
