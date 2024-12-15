package com.prograWeb.sistemabecas.model;

import org.springframework.data.annotation.Id;
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
    private List<UsuarioSolicitud> usuarios = new ArrayList<>();

}
