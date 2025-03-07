package com.example.demo.dto;

public class RegisterResponseDTO {
    private String message;
    private String error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RegisterResponseDTO(String message, String error) {
        this.message = message;
        this.error=error;
    }



}
