package innovaschools.com.gestion_laboratorio.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import innovaschools.com.gestion_laboratorio.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String ruta = request.getServletPath();

    return !ruta.startsWith("/api/equipos")
            && !ruta.startsWith("/api/reservas");
}


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String autorizacion = request.getHeader("Authorization");

        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Debe enviar un token JWT"
            );
            return;
        }

        String token = autorizacion.substring(7);

        if (!jwtService.tokenValido(token)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido o vencido"
            );
            return;
        }

        request.setAttribute("correoUsuario",
                jwtService.obtenerCorreo(token));

        request.setAttribute("rolUsuario",
                jwtService.obtenerRol(token));

        filterChain.doFilter(request, response);
    }
}
