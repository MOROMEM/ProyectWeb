package com.prograWeb.sistemabecas.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.prograWeb.sistemabecas.model.Solicitud;

@Repository
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {

    // Buscar solicitudes sin usuarios asociados
    @Query("{ 'usuarios': { $size: 0 } }")
    List<Solicitud> findWithoutUsuarios();

    // Buscar solicitudes donde un usuario específico está asociado
    @Query("{ 'usuarios.usuarioId': ?0 }")
    List<Solicitud> findByUsuarioId(String usuarioId);

    // Filtro por usuario
    @Query("{ 'usuarios.usuarioId': ?0 }")
    List<Solicitud> findByUsuarios_UsuarioId(String usuarioId);

    @Query("{ '$or': [ { 'usuarios.usuarioId': ?0 }, { 'usuarios': { $size: 0 } } ] }")
    List<Solicitud> findByUsuarios_UsuarioIdOrUsuariosEmpty(String usuarioId);

}
