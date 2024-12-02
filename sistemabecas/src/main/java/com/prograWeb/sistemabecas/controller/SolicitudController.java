package com.prograWeb.sistemabecas.controller;


import java.util.List; // Para @RestController

import org.springframework.http.ResponseEntity; // Para @RequestMapping
import org.springframework.security.access.prepost.PreAuthorize; // Para @PostMapping
import org.springframework.web.bind.annotation.GetMapping; // Para @GetMapping
import org.springframework.web.bind.annotation.PostMapping; // Para ResponseEntity
import org.springframework.web.bind.annotation.RequestBody; // Para @RequestBody
import org.springframework.web.bind.annotation.RequestMapping; // Para @PreAuthorize
import org.springframework.web.bind.annotation.RestController;

import com.prograWeb.sistemabecas.model.Solicitud;

@RestController
@RequestMapping("/solicitudes")
@PreAuthorize("hasRole('USER')")
public class SolicitudController {
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud) {
        // Crear nueva solicitud
        return ResponseEntity.ok("Solicitud creada correctamente.");
    }

    @GetMapping
    public List<Solicitud> listarSolicitudes() {
        // Listar solicitudes
        return List.of(new Solicitud());
    }
}
