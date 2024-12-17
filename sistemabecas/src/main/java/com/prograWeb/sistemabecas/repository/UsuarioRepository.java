package com.prograWeb.sistemabecas.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.prograWeb.sistemabecas.model.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    // Buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);

    // Verificar si un usuario con un email ya existe
    boolean existsByEmail(String email);


}
