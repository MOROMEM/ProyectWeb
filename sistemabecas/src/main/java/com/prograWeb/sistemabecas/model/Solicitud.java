package com.prograWeb.sistemabecas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; 

import lombok.Data;

@Data
@Document(collection = "solicitudes")
public class Solicitud {
    @Id
    private String id;
    private String usuarioId;
    private String descripcion;
    private String estado;
}
