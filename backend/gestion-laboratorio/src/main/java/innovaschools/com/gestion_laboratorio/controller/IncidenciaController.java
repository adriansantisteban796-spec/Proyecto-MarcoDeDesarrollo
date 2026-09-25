package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Incidencia;
import innovaschools.com.gestion_laboratorio.repository.IncidenciaRepository;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    private final IncidenciaRepository incidenciaRepository;

    public IncidenciaController(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    // RF-10: Reportar incidencia
    @PostMapping
    public ResponseEntity<?> reportarIncidencia(@RequestBody Incidencia incidencia) {
        incidencia.setFecha_reporte(LocalDateTime.now());
        incidencia.setEstado("ABIERTA");
        return ResponseEntity.ok(incidenciaRepository.save(incidencia));
    }

    // RF-11: Listar incidencias (filtro opcional)
    @GetMapping
    public ResponseEntity<?> listarIncidencias(
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(incidenciaRepository.filtrarIncidencias(idEquipo, estado));
    }
}