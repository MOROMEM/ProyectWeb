package com.prograWeb.sistemabecas.controller;

import com.prograWeb.sistemabecas.model.Solicitud;
import com.prograWeb.sistemabecas.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud) {
        Solicitud nuevaSolicitud = solicitudRepository.save(solicitud);
        return ResponseEntity.ok(nuevaSolicitud);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSolicitud(@PathVariable String id, @RequestBody Solicitud solicitudActualizada) {
        // Buscar la solicitud por ID
        Optional<Solicitud> solicitudExistente = solicitudRepository.findById(id);

        // Si no se encuentra, devolver un error 404
        if (solicitudExistente.isEmpty()) {
            return ResponseEntity.status(404).body("Solicitud no encontrada");
        }

        // Modificar la solicitud existente
        Solicitud solicitud = solicitudExistente.get();
        solicitud.setDescripcion(solicitudActualizada.getDescripcion());
        solicitud.setEstado(solicitudActualizada.getEstado());
        solicitud.setUsuarioId(solicitudActualizada.getUsuarioId());

        // Guardar la solicitud actualizada
        Solicitud actualizada = solicitudRepository.save(solicitud);

        // Devolver la solicitud actualizada
        return ResponseEntity.ok(actualizada);
    }


    // Eliminar una solicitud
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable String id) {
        return solicitudRepository.findById(id).map(solicitud -> {
            solicitudRepository.delete(solicitud);
            return ResponseEntity.ok("Solicitud eliminada exitosamente");
        }).orElse(ResponseEntity.status(404).body("Solicitud no encontrada"));
    }
}

