package com.prograWeb.sistemabecas.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;



@Data
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    private String nombre;
    private String email;
    private String password;
    private String usuarioId;
    private boolean admin;
}




