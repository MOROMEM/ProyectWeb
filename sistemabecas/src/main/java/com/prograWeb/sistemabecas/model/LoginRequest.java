package com.prograWeb.sistemabecas.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

    // Constructor vacío (necesario para deserialización de JSON)
    public LoginRequest() {
    }

    // Constructor con parámetros
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}