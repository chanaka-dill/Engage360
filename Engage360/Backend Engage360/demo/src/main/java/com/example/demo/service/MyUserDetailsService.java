package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByEmail(email).orElse(null);

        if(userData==null)throw new UsernameNotFoundException("User Not Found");

        UserDetails user= User.builder()
                .username(userData.getEmail())
                .password(userData.getPassword())
                .build();
        return user;
    }
}
