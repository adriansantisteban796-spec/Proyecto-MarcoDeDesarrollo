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

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {

        Usuario usuarioExistente =
                usuarioRepository.findByCorreo(usuario.getCorreo());

        if (usuarioExistente != null) {
            return ResponseEntity.badRequest()
                    .body("El correo electrónico ya está registrado");
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return ResponseEntity.ok(nuevoUsuario);
    }
}