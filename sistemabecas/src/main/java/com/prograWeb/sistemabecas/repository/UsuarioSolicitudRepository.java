package com.prograWeb.sistemabecas.repository;



import com.prograWeb.sistemabecas.model.UsuarioSolicitud;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioSolicitudRepository extends MongoRepository<UsuarioSolicitud, String> {
}
