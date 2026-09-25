package innovaschools.com.gestion_laboratorio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import innovaschools.com.gestion_laboratorio.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByCorreo(String correo);
}