package com.prograWeb.sistemabecas.controller;

import com.prograWeb.sistemabecas.dto.SolicitudEnriquecida;
import com.prograWeb.sistemabecas.model.Solicitud;
import com.prograWeb.sistemabecas.model.Usuario;
import com.prograWeb.sistemabecas.model.UsuarioSolicitud;
import com.prograWeb.sistemabecas.repository.SolicitudRepository;
import com.prograWeb.sistemabecas.repository.UsuarioRepository;
import com.prograWeb.sistemabecas.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud, @RequestHeader("Authorization") String token) {
        try {
            // Extraer el email desde el token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autorizado."));

            // Solo los administradores pueden crear solicitudes
            if (!usuario.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para crear una solicitud.");
            }

            // Crear una nueva solicitud (la lista de usuarios empieza vacía)
            Solicitud nuevaSolicitud = new Solicitud();
            nuevaSolicitud.setDescripcion(solicitud.getDescripcion());
            nuevaSolicitud.setUsuarios(new ArrayList<>()); // Inicializamos la lista vacía

            solicitudRepository.save(nuevaSolicitud);

            return ResponseEntity.ok(nuevaSolicitud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la solicitud: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> listarSolicitudes(@RequestHeader("Authorization") String token) {
        try {
            // Extraer email desde el token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            boolean isAdmin = usuario.isAdmin();
            List<Solicitud> solicitudes;

            if (isAdmin) {
                // Administradores ven todas las solicitudes
                solicitudes = solicitudRepository.findAll();
            } else {
                // Usuarios normales
                solicitudes = solicitudRepository.findAll().stream()
                        .filter(solicitud -> {
                            // Incluir solicitudes:
                            // - Asociadas al usuario actual
                            // - O no asociadas a este usuario
                            boolean usuarioAsociado = solicitud.getUsuarios().stream()
                                    .anyMatch(us -> us.getUsuarioId().equals(usuario.getId()));
                            return !usuarioAsociado || solicitud.getUsuarios().isEmpty();
                        })
                        .toList();
            }

            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener solicitudes: " + e.getMessage());
        }
    }




    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoUsuarioEnSolicitud(
            @PathVariable String id,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        try {
            // Extraer el email del token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autorizado."));

            // Obtener el nuevo estado del cuerpo de la petición
            String nuevoEstado = payload.get("estado");
            String usuarioId = payload.get("usuarioId"); // ID del usuario al que actualizar el estado

            // Buscar la solicitud por ID
            Solicitud solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));

            // Buscar al usuario dentro de la lista de usuarios asociados a la solicitud
            boolean usuarioActualizado = false;
            for (UsuarioSolicitud usuarioSolicitud : solicitud.getUsuarios()) {
                if (usuarioSolicitud.getUsuarioId().equals(usuarioId)) {
                    usuarioSolicitud.setEstado(nuevoEstado); // Actualizar el estado
                    usuarioActualizado = true;
                    break;
                }
            }

            if (!usuarioActualizado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El usuario no está asociado a esta solicitud.");
            }

            // Guardar la solicitud con el estado actualizado
            solicitudRepository.save(solicitud);

            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el estado de la solicitud: " + e.getMessage());
        }
    }


    @PutMapping("/{id}/asociar-usuario")
    public ResponseEntity<?> asociarUsuarioASolicitud(@PathVariable String id, @RequestHeader("Authorization") String token) {
        try {
            // Extraer usuarioId desde el token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(id);

            if (optionalSolicitud.isPresent()) {
                Solicitud solicitud = optionalSolicitud.get();

                // Buscar si el usuario ya está asociado
                boolean usuarioExistente = false;

                for (UsuarioSolicitud us : solicitud.getUsuarios()) {
                    if (us.getUsuarioId().equals(usuario.getId())) {
                        us.setEstado("pendiente"); // Actualizar estado
                        usuarioExistente = true;
                        break;
                    }
                }

                // Si no existe, agregar al array
                if (!usuarioExistente) {
                    solicitud.getUsuarios().add(new UsuarioSolicitud(usuario.getId(), "pendiente"));
                }

                solicitudRepository.save(solicitud);
                return ResponseEntity.ok(solicitud);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al asociar usuario: " + e.getMessage());
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
