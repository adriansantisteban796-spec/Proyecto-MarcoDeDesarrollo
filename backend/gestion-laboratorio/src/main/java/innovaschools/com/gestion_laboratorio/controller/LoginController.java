package innovaschools.com.gestion_laboratorio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.LoginRequest;
import innovaschools.com.gestion_laboratorio.model.Usuario;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;
import innovaschools.com.gestion_laboratorio.service.JwtService;
import innovaschools.com.gestion_laboratorio.service.PasswordService;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public LoginController(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            PasswordService passwordService) {

        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
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

    if (!passwordService.coincide(
            loginRequest.getPassword(), usuario.getPassword())) {
        return ResponseEntity.status(401)
                .body("Correo o contraseña incorrectos");
    }

    // Las claves guardadas sin cifrar se cifran en el primer inicio de sesion
    if (!passwordService.estaCifrada(usuario.getPassword())) {
        usuario.setPassword(passwordService.cifrar(loginRequest.getPassword()));
        usuarioRepository.save(usuario);
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
