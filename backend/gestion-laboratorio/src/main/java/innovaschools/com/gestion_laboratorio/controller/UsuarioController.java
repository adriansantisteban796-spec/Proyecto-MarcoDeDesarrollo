package innovaschools.com.gestion_laboratorio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.Usuario;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Registrar usuario
    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioExistente =
                usuarioRepository.findByCorreo(usuario.getCorreo());

        if (usuarioExistente != null) {
            return ResponseEntity.badRequest()
                    .body("El correo electrónico ya está registrado");
        }

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
        usuario.setPassword(datos.getPassword());
        usuario.setRol(datos.getRol());

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