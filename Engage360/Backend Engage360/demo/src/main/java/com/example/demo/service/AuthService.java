package com.example.demo.service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.RegisterResponseDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }

    public UserEntity createUser(RegisterRequestDTO userData){
        UserEntity newUser=new UserEntity(userData.getName(),
                userData.getEmail(),
                userData.getUsername(),
                userData.getRole(),
                passwordEncoder.encode(userData.getPassword()));
                return userRepository.save(newUser);
    }

    public LoginResponseDTO login(LoginRequestDTO loginData) {
        UserEntity user = userRepository.findByEmail(loginData.getEmail()).orElse(null);

        if (user == null) {
            return new LoginResponseDTO(null, null, "User not found", "error");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), loginData.getPassword())
            );
        } catch (Exception e) {
            return new LoginResponseDTO(null, null, "Invalid credentials", "error");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String token = jwtService.getJWTToken(user.getEmail(), claims);  // Generate token using email
        return new LoginResponseDTO(token, LocalDateTime.now(), null, "Token generated successfully");
    }


    public RegisterResponseDTO register(RegisterRequestDTO req){
        if(isUserEnable(req.getUsername())) return new RegisterResponseDTO(null,"user already exits in the system ");

        var userData =this.createUser(req);
        if(userData.getId()==null) return new RegisterResponseDTO(null,"System error ");

        return new RegisterResponseDTO(String.format("user registed at %s ",userData.getId()),null);
    }

    private Boolean isUserEnable(String email){
        return userRepository.findByEmail(email).isPresent();
    }

}
