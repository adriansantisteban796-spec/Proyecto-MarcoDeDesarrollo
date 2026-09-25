package innovaschools.com.gestion_laboratorio.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.repository.*;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final PrestamoRepository prestamoRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final IncidenciaRepository incidenciaRepository;

    public HistorialController(PrestamoRepository prestamoRepository,
                                MantenimientoRepository mantenimientoRepository,
                                IncidenciaRepository incidenciaRepository) {
        this.prestamoRepository = prestamoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.incidenciaRepository = incidenciaRepository;
    }

    @GetMapping("/{idEquipo}")
    public ResponseEntity<?> historialEquipo(@PathVariable Integer idEquipo) {
        Map<String, Object> historial = new HashMap<>();
        historial.put("prestamos", prestamoRepository.filtrarPrestamos(idEquipo, null));
        historial.put("mantenimientos", mantenimientoRepository.findAll().stream()
                .filter(m -> m.getId_equipo().equals(idEquipo)).toList());
        historial.put("incidencias", incidenciaRepository.filtrarIncidencias(idEquipo, null));
        return ResponseEntity.ok(historial);
    }
}