package innovaschools.com.gestion_laboratorio.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Nombres de laboratorios y categorias para mostrar los equipos
// (la tabla equipo solo guarda sus ids)
@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private List<Map<String, Object>> consultar(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            // Si la tabla no existe se devuelve vacio y la pantalla muestra los ids
            return List.of();
        }
    }

    @GetMapping
    public ResponseEntity<?> listarCatalogos() {

        Map<String, Object> catalogos = new LinkedHashMap<>();

        catalogos.put("laboratorios", consultar(
                "SELECT id_laboratorio AS id, codigo, nombre "
                + "FROM laboratorio ORDER BY id_laboratorio"));

        catalogos.put("categorias", consultar(
                "SELECT id_categoria AS id, nombre "
                + "FROM categoria ORDER BY nombre"));

        return ResponseEntity.ok(catalogos);
    }
}
