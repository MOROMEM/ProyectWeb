package com.prograWeb.sistemabecas.controller;


import com.prograWeb.sistemabecas.model.Usuario;
import com.prograWeb.sistemabecas.model.LoginRequest;
import com.prograWeb.sistemabecas.repository.UsuarioRepository;
import com.prograWeb.sistemabecas.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * Método para registrar un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        // Validar si el correo ya está registrado
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }

        // Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    /**
     * Método para autenticar a un usuario y generar un token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Buscar al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElse(null);

        // Validar credenciales
        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        // Generar JWT
        String token = jwtUtil.generateToken(usuario.getEmail());

        // Devolver el token al cliente
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("id", usuario.getId());
        return ResponseEntity.ok(response);
    }
}

