package com.prograWeb.sistemabecas.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class SolicitudEnriquecida {
    private String id;
    private String descripcion;
    private List<UsuarioInfo> usuarios;

    public SolicitudEnriquecida(String id, String descripcion, List<UsuarioInfo> usuarios) {
        this.id = id;
        this.descripcion = descripcion;
        this.usuarios = usuarios;
    }



    @Data
    public static class UsuarioInfo {
        private String usuarioId;
        private String nombre;
        private String estado;

        public UsuarioInfo(String usuarioId, String nombre, String estado) {
            this.usuarioId = usuarioId;
            this.nombre = nombre;
            this.estado = estado;
        }

    }
}
