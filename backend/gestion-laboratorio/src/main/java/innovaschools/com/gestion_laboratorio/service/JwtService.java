package innovaschools.com.gestion_laboratorio.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final String claveSecreta = "EstaEsUnaClaveSecretaParaMiSistema123456789";

    public String generarToken(String correo, String rol) {

        SecretKey key = Keys.hmacShaKeyFor(claveSecreta.getBytes());

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
