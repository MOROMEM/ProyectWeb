package com.prograWeb.sistemabecas.controller;

import com.prograWeb.sistemabecas.dto.SolicitudEnriquecida;
import com.prograWeb.sistemabecas.model.Solicitud;
import com.prograWeb.sistemabecas.model.Usuario;
import com.prograWeb.sistemabecas.model.UsuarioSolicitud;
import com.prograWeb.sistemabecas.repository.SolicitudRepository;
import com.prograWeb.sistemabecas.repository.UsuarioRepository;
import com.prograWeb.sistemabecas.repository.UsuarioSolicitudRepository;
import com.prograWeb.sistemabecas.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioSolicitudRepository usuarioSolicitudRepository;

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


    @PutMapping("/{solicitudId}/usuarios/{usuarioId}/estado")
    public ResponseEntity<?> actualizarEstadoUsuarioEnSolicitud(
            @PathVariable String solicitudId,
            @PathVariable String usuarioId,
            @RequestBody Map<String, String> payload) {
        try {
            String nuevoEstado = payload.get("estado"); // Asegúrate de que payload contiene "estado"

            // Verificar si solicitud existe
            Solicitud solicitud = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            // Buscar relación UsuarioSolicitud
            UsuarioSolicitud usuarioSolicitud = solicitud.getUsuarios().stream()
                    .filter(us -> us.getUsuario().getId().equals(usuarioId)) // Revisar si getUsuario() es null
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Usuario no asociado a esta solicitud"));
//cambiar getId() y ver que pasa
            // Actualizar estado
            usuarioSolicitud.setEstado(nuevoEstado);

            // Guardar cambios
            usuarioSolicitudRepository.save(usuarioSolicitud);

            return ResponseEntity.ok("Estado actualizado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la excepción en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el estado: " + e.getMessage());
        }
    }


    @PutMapping("/{id}/asociar-usuario")
    public ResponseEntity<?> asociarUsuarioASolicitud(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            // Extraer el email del token
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar la solicitud
            Solicitud solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            // Verificar si el usuario ya está asociado
            UsuarioSolicitud usuarioSolicitudExistente = solicitud.getUsuarios().stream()
                    .filter(us -> us.getUsuario().getId().equals(usuario.getId()))
                    .findFirst()
                    .orElse(null);

            if (usuarioSolicitudExistente != null) {
                // Actualizar el estado si ya existe
                usuarioSolicitudExistente.setEstado("pendiente");
            } else {
                // Crear una nueva relación UsuarioSolicitud
                UsuarioSolicitud nuevaRelacion = new UsuarioSolicitud(usuario, "pendiente");
                usuarioSolicitudRepository.save(nuevaRelacion); // Guardar relación en DB

                // Asociar la relación guardada a la solicitud
                solicitud.getUsuarios().add(nuevaRelacion);
            }

            // Guardar la solicitud
            solicitudRepository.save(solicitud);

            return ResponseEntity.ok("Usuario asociado correctamente.");
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


    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarSolicitudesAdmin() {
        try {
            List<Solicitud> solicitudes = solicitudRepository.findAll();

            // Mapear a SolicitudEnriquecida, excluyendo relaciones inválidas
            List<SolicitudEnriquecida> solicitudesEnriquecidas = solicitudes.stream()
                    .map(solicitud -> {
                        List<SolicitudEnriquecida.UsuarioInfo> usuariosInfo = solicitud.getUsuarios().stream()
                                .filter(us -> us.getUsuario() != null) // Excluir relaciones con usuarios nulos
                                .map(us -> new SolicitudEnriquecida.UsuarioInfo(
                                        us.getUsuario().getId(),
                                        us.getUsuario().getNombre(),
                                        us.getEstado()
                                ))
                                .toList();

                        return new SolicitudEnriquecida(
                                solicitud.getId(),
                                solicitud.getDescripcion(),
                                usuariosInfo
                        );
                    })
                    .toList();

            return ResponseEntity.ok(solicitudesEnriquecidas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener solicitudes: " + e.getMessage());
        }
    }



    @GetMapping("/usuario/no-asociadas")
    public ResponseEntity<?> obtenerSolicitudesNoAsociadas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Solicitud> solicitudes = solicitudRepository.findAll();

            // Filtrar solicitudes no asociadas al usuario
            List<Solicitud> noAsociadas = solicitudes.stream()
                    .filter(solicitud -> solicitud.getUsuarios().stream()
                            .noneMatch(us -> us.getUsuario() != null && us.getUsuario().getId().equals(usuario.getId())))
                    .toList();

            return ResponseEntity.ok(noAsociadas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener solicitudes no asociadas: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/asociadas")
    public ResponseEntity<?> obtenerSolicitudesAsociadas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Filtrar solicitudes asociadas al usuario
            List<Solicitud> asociadas = solicitudRepository.findAll().stream()
                    .filter(solicitud -> solicitud.getUsuarios().stream()
                            .anyMatch(us -> us.getUsuario() != null && us.getUsuario().getId().equals(usuario.getId())))
                    .toList();

            return ResponseEntity.ok(asociadas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener solicitudes asociadas: " + e.getMessage());
        }
    }






}
