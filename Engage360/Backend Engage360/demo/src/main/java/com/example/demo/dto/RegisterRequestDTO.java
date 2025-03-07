package com.example.demo.dto;


public class RegisterRequestDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String role;



    public RegisterRequestDTO(String firstname,String lastname, String email, String username, String password, String role) {
        this.firstname = firstname;
        this.lastname=lastname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role=role;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
