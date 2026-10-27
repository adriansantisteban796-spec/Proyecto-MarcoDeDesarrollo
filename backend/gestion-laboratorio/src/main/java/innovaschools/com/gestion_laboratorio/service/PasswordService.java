package innovaschools.com.gestion_laboratorio.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String cifrar(String password) {
        return encoder.encode(password);
    }

    // Los hashes BCrypt empiezan con "$2"; lo demas es una clave guardada antes del cifrado
    public boolean estaCifrada(String almacenada) {
        return almacenada != null && almacenada.startsWith("$2");
    }

    public boolean coincide(String ingresada, String almacenada) {
        if (ingresada == null || almacenada == null) {
            return false;
        }

        if (estaCifrada(almacenada)) {
            return encoder.matches(ingresada, almacenada);
        }

        return almacenada.equals(ingresada);
    }
}
