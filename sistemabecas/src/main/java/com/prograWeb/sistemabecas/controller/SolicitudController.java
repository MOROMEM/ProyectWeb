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
import java.util.Map;
import java.util.Optional;

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
            String email = jwtUtil.getUsernameFromToken(token.substring(7)); // Extraemos el email del token
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autorizado."));

            // Solo asignar usuarioId si el usuario no es admin
            if (!usuario.isAdmin()) {
                solicitud.setUsuarioId(usuario.getId());
            }

            solicitud.setEstado("pendiente"); // Estado predeterminado
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
    public ResponseEntity<?> listarSolicitudes(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            boolean isAdmin = usuario.isAdmin();

            List<Solicitud> solicitudes;
            if (isAdmin) {
                solicitudes = solicitudRepository.findAll(); // Admin ve todo
            } else {
                solicitudes = solicitudRepository.findByUsuarioIdOrUsuarioIdIsNull(usuario.getId());
            }

            System.out.println("Solicitudes enviadas al frontend: " + solicitudes); // Debug log
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al obtener solicitudes: " + e.getMessage());
        }
    }



    @PutMapping("/{id}/estado")
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

    @PutMapping("/{id}/asociar-usuario")
    public ResponseEntity<?> asociarUsuarioASolicitud(@PathVariable String id, @RequestHeader("Authorization") String token) {
        try {
            // Extraer el email del token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));

            // Buscar al usuario por su email
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar la solicitud por ID
            Solicitud solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            // Asociar el usuario logueado a la solicitud
            solicitud.setUsuarioId(usuario.getId());
            solicitud.setEstado("pendiente"); // Opcional: Puedes definir un estado predeterminado

            // Guardar la solicitud actualizada
            solicitudRepository.save(solicitud);

            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al asociar usuario a la solicitud: " + e.getMessage());
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
