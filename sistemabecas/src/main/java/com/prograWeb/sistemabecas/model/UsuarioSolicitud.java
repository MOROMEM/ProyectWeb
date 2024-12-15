package com.prograWeb.sistemabecas.model;

import lombok.Data;

@Data
public class UsuarioSolicitud {
    private String usuarioId;
    private String estado;

    public UsuarioSolicitud() {
    }

    public UsuarioSolicitud(String usuarioId, String estado) {
        this.usuarioId = usuarioId;
        this.estado = estado;
    }
}