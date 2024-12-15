package com.prograWeb.sistemabecas.controller;

import com.prograWeb.sistemabecas.model.Solicitud;
import com.prograWeb.sistemabecas.model.Usuario;
import com.prograWeb.sistemabecas.repository.SolicitudRepository;
import com.prograWeb.sistemabecas.repository.UsuarioRepository;
import com.prograWeb.sistemabecas.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud, @RequestHeader("Authorization") String token) {
        try {
            // Extraer el usuarioId a partir del token JWT
            String email = jwtUtil.getUsernameFromToken(token.substring(7)); // Quitamos el prefijo "Bearer "
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado.");
            }

            // Asignar el usuarioId a la solicitud
            solicitud.setUsuarioId(usuario.getId());
            solicitud.setEstado("pendiente"); // Estado predeterminado al crear
            solicitudRepository.save(solicitud);

            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la solicitud: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerSolicitudesPorUsuario(@PathVariable String usuarioId) {
        try {
            List<Solicitud> solicitudes = solicitudRepository.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener solicitudes: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        List<Solicitud> solicitudes = solicitudRepository.findAll();
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstadoSolicitud(@PathVariable String id, @RequestBody Solicitud solicitudActualizada) {
        try {
            // Buscar la solicitud por ID
            Solicitud solicitud = solicitudRepository.findById(id).orElse(null);

            if (solicitud == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada.");
            }

            // Actualizar el estado de la solicitud
            solicitud.setEstado(solicitudActualizada.getEstado());
            solicitudRepository.save(solicitud);

            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la solicitud: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable String id) {
        try {
            // Buscar la solicitud por ID
            Solicitud solicitud = solicitudRepository.findById(id).orElse(null);

            if (solicitud == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada.");
            }

            // Eliminar la solicitud
            solicitudRepository.deleteById(id);

            return ResponseEntity.ok("Solicitud eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la solicitud: " + e.getMessage());
        }
    }



}
