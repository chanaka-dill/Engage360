package com.example.demo.controller;


import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.RegisterResponseDTO;
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
        this.authService=authService;
    }


    @GetMapping
    public List<UserEntity> getAllUsers(){
        return authService.getAllUsers();
    }
    @PostMapping
    public UserEntity createUser(@RequestBody RegisterRequestDTO user){
        return authService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO  loginData) {
        LoginResponseDTO res=authService.login(loginData);
       if (res.getError()!=null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

       return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> login(@RequestBody RegisterRequestDTO req) {
        RegisterResponseDTO res=authService.register(req);
        if (res.getError()!=null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }




}
