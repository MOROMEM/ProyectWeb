package com.prograWeb.sistemabecas.model;


import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "usuario_solicitud")
public class UsuarioSolicitud {

    @Id
    private String id;

    @DBRef(lazy = false) // Asegura la carga del usuario
    private Usuario usuario;

    private String estado;

    public UsuarioSolicitud(Usuario usuario, String estado) {
        this.usuario = usuario;
        this.estado = estado;
    }
}
