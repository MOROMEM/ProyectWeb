package com.prograWeb.sistemabecas.controller;

import com.prograWeb.sistemabecas.model.Solicitud;
import com.prograWeb.sistemabecas.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepository;

    // Obtener todas las solicitudes
    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        List<Solicitud> solicitudes = solicitudRepository.findAll();
        return ResponseEntity.ok(solicitudes);
    }

    // Buscar solicitudes por usuarioId
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Solicitud>> buscarPorUsuarioId(@PathVariable String usuarioId) {
        List<Solicitud> solicitudes = solicitudRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(solicitudes);
    }

    // Filtrar solicitudes por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Solicitud>> filtrarPorEstado(@PathVariable String estado) {
        List<Solicitud> solicitudes = solicitudRepository.findByEstado(estado);
        return ResponseEntity.ok(solicitudes);
    }
}

