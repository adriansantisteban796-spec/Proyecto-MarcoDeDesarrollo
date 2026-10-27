package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Incidencia;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.repository.IncidenciaRepository;
import innovaschools.com.gestion_laboratorio.service.EquipoEstadoService;
import innovaschools.com.gestion_laboratorio.service.JwtService;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    private static final List<String> SEVERIDADES =
            List.of("BAJA", "MEDIA", "ALTA", "CRITICA");

    private final IncidenciaRepository incidenciaRepository;
    private final EquipoRepository equipoRepository;
    private final EquipoEstadoService equipoEstadoService;
    private final JwtService jwtService;

    public IncidenciaController(
            IncidenciaRepository incidenciaRepository,
            EquipoRepository equipoRepository,
            EquipoEstadoService equipoEstadoService,
            JwtService jwtService) {
        this.incidenciaRepository = incidenciaRepository;
        this.equipoRepository = equipoRepository;
        this.equipoEstadoService = equipoEstadoService;
        this.jwtService = jwtService;
    }

    // RF-10: Reportar incidencia
    // Si la severidad es CRITICA el equipo pasa a "No Disponible"
    @PostMapping
    public ResponseEntity<?> reportarIncidencia(
            @RequestBody Incidencia incidencia,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (incidencia.getId_equipo() == null
                || incidencia.getDescripcion() == null
                || incidencia.getDescripcion().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Debe indicar el equipo y la descripción");
        }

        if (!equipoRepository.existsById(incidencia.getId_equipo())) {
            return ResponseEntity.badRequest()
                    .body("El equipo indicado no existe");
        }

        String severidad = incidencia.getSeveridad() == null
                || incidencia.getSeveridad().isBlank()
                ? "MEDIA"
                : incidencia.getSeveridad().toUpperCase();

        if (!SEVERIDADES.contains(severidad)) {
            return ResponseEntity.badRequest()
                    .body("La severidad debe ser BAJA, MEDIA, ALTA o CRITICA");
        }

        // Si no se indica quien reporta se usa el usuario del token
        if ((incidencia.getReportado_por() == null
                || incidencia.getReportado_por().isBlank())
                && authorization != null
                && authorization.startsWith("Bearer ")) {
            incidencia.setReportado_por(
                    jwtService.obtenerCorreo(authorization.substring(7)));
        }

        incidencia.setId_incidencia(null);
        incidencia.setSeveridad(severidad);
        incidencia.setFecha_reporte(LocalDateTime.now());
        incidencia.setEstado("ABIERTA");

        Incidencia guardada = incidenciaRepository.save(incidencia);

        equipoEstadoService.recalcular(guardada.getId_equipo());

        return ResponseEntity.ok(guardada);
    }

    // RF-11: Listar incidencias (filtro opcional)
    @GetMapping
    public ResponseEntity<?> listarIncidencias(
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(incidenciaRepository.filtrarIncidencias(idEquipo, estado));
    }

    // Actualizar incidencia (al resolver una critica el equipo se libera)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarIncidencia(@PathVariable Integer id,
            @RequestBody Incidencia incidenciaDetalles) {

        Optional<Incidencia> opt = incidenciaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String severidad = incidenciaDetalles.getSeveridad();

        if (severidad != null && !SEVERIDADES.contains(severidad.toUpperCase())) {
            return ResponseEntity.badRequest()
                    .body("La severidad debe ser BAJA, MEDIA, ALTA o CRITICA");
        }

        Incidencia incidencia = opt.get();

        if (incidenciaDetalles.getDescripcion() != null) {
            incidencia.setDescripcion(incidenciaDetalles.getDescripcion());
        }

        if (incidenciaDetalles.getEstado() != null) {
            incidencia.setEstado(incidenciaDetalles.getEstado().toUpperCase());
        }

        if (incidenciaDetalles.getReportado_por() != null) {
            incidencia.setReportado_por(incidenciaDetalles.getReportado_por());
        }

        if (severidad != null) {
            incidencia.setSeveridad(severidad.toUpperCase());
        }

        Incidencia actualizada = incidenciaRepository.save(incidencia);

        equipoEstadoService.recalcular(actualizada.getId_equipo());

        return ResponseEntity.ok(actualizada);
    }

    // Eliminar incidencia
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarIncidencia(@PathVariable Integer id) {

        Optional<Incidencia> opt = incidenciaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Integer idEquipo = opt.get().getId_equipo();

        incidenciaRepository.deleteById(id);

        equipoEstadoService.recalcular(idEquipo);

        return ResponseEntity.ok().build();
    }
}
