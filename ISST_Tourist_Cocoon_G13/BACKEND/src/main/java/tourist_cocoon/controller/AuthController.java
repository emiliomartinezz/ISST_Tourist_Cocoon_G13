package com.touristcocoon.controller;

import com.touristcocoon.model.Usuario;
import com.touristcocoon.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Para permitir peticiones desde React
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public Usuario registrar(@RequestBody Usuario usuario) {
        // En una fase real aquí cifraríamos la contraseña
        return usuarioRepository.save(usuario);
    }
}