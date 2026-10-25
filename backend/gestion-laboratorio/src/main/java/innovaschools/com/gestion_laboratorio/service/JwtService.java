
package innovaschools.com.gestion_laboratorio.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final String claveSecreta =
            "EstaEsUnaClaveSecretaParaMiSistema123456789";

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(claveSecreta.getBytes());
    }

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(obtenerClave())
                .compact();
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerRol(String token) {
        try {
            return obtenerClaims(token).get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String obtenerCorreo(String token) {
        try {
            return obtenerClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getSubject() != null
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
