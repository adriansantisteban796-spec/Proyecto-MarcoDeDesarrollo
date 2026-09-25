package innovaschools.com.gestion_laboratorio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.LoginRequest;
import innovaschools.com.gestion_laboratorio.model.Usuario;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;
import innovaschools.com.gestion_laboratorio.service.JwtService;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public LoginController(
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest loginRequest) {

        Usuario usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo());

        if (usuario == null) {
            return ResponseEntity.status(401)
                    .body("Correo o contraseña incorrectos");
        }

        if (!usuario.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401)
                    .body("Correo o contraseña incorrectos");
        }

        String token = jwtService.generarToken(
                usuario.getCorreo(),
                usuario.getRol()
        );

        return ResponseEntity.ok(token);
    }
}