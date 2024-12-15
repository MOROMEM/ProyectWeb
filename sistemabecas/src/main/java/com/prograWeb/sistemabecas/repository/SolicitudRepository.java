package com.prograWeb.sistemabecas.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.prograWeb.sistemabecas.model.Solicitud;

@Repository
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {

    // Método para buscar solicitudes por el ID del usuario
    List<Solicitud> findByUsuarioId(String usuarioId);

    // Método para filtrar solicitudes por estado
    List<Solicitud> findByEstado(String estado);

    // Método para filtrar por usuarioId
    List<Solicitud> findByUsuarioIdOrUsuarioIdIsNull(String usuarioId);
}
