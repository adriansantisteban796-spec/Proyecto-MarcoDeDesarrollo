package innovaschools.com.gestion_laboratorio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.LoginRequest;
import innovaschools.com.gestion_laboratorio.model.Usuario;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;
import innovaschools.com.gestion_laboratorio.service.JwtService;

import java.util.Map;

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

    if (loginRequest == null
            || loginRequest.getCorreo() == null
            || loginRequest.getCorreo().isBlank()
            || loginRequest.getPassword() == null
            || loginRequest.getPassword().isBlank()) {

        return ResponseEntity.badRequest()
                .body("Debe ingresar correo y contraseña");
    }

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

@GetMapping("/usuario")
public ResponseEntity<?> obtenerUsuarioAutenticado(
        @RequestHeader(value = "Authorization", required = false) String authorization) {

    if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(401)
                .body("Debe enviar un token JWT");
    }

    String token = authorization.substring(7);

    if (!jwtService.tokenValido(token)) {
        return ResponseEntity.status(401)
                .body("Token inválido o expirado");
    }

    String correo = jwtService.obtenerCorreo(token);
    String rol = jwtService.obtenerRol(token);

    return ResponseEntity.ok(Map.of(
            "correo", correo,
            "rol", rol
    ));
}
}

