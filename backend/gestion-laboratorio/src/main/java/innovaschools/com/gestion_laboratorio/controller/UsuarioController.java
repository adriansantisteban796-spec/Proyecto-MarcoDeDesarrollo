package innovaschools.com.gestion_laboratorio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.Usuario;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;
import innovaschools.com.gestion_laboratorio.service.PasswordService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final String dominioPermitido;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PasswordService passwordService,
            @Value("${app.correo.dominio}") String dominioPermitido) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
        this.dominioPermitido = dominioPermitido;
    }

    // Registrar usuario
    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {

        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Debe ingresar el correo electrónico");
        }

        Usuario usuarioExistente =
                usuarioRepository.findByCorreo(usuario.getCorreo());

        if (usuarioExistente != null) {
            return ResponseEntity.badRequest()
                    .body("El correo electrónico ya está registrado");
        }

        // Solo se aceptan correos institucionales
        if (!usuario.getCorreo().toLowerCase().endsWith("@" + dominioPermitido)) {
            return ResponseEntity.badRequest()
                    .body("Solo se permiten correos institucionales (@"
                            + dominioPermitido + ")");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Debe ingresar una contraseña");
        }

        usuario.setPassword(passwordService.cifrar(usuario.getPassword()));

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return ResponseEntity.status(201).body(nuevoUsuario);
    }

    // Listar usuarios
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // Buscar usuario por correo
    @GetMapping("/{correo}")
    public ResponseEntity<?> buscarUsuario(@PathVariable String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    // Actualizar usuario
    @PutMapping("/{correo}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable String correo,
            @RequestBody Usuario datos) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setRol(datos.getRol());

        // Si no se envia contraseña se conserva la actual
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordService.cifrar(datos.getPassword()));
        }

        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    // Eliminar usuario
    @DeleteMapping("/{correo}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.delete(usuario);

        return ResponseEntity.noContent().build();
    }
}
