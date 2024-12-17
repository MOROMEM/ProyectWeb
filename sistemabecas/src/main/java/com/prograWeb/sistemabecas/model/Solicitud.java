package com.prograWeb.sistemabecas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;



@Data
@Document(collection = "solicitudes")
public class Solicitud {

    @Id
    private String id;

    private String descripcion;

    @DBRef // Relaciona con la lista de UsuarioSolicitud
    private List<UsuarioSolicitud> usuarios;
}