package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Mantenimiento;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.repository.MantenimientoRepository;
import innovaschools.com.gestion_laboratorio.service.EquipoEstadoService;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private static final List<String> TIPOS = List.of("PREVENTIVO", "CORRECTIVO");
    private static final List<String> ESTADOS_CERRADOS =
            List.of("FINALIZADO", "COMPLETADO", "CANCELADO");

    private final MantenimientoRepository mantenimientoRepository;
    private final EquipoRepository equipoRepository;
    private final EquipoEstadoService equipoEstadoService;

    public MantenimientoController(
            MantenimientoRepository mantenimientoRepository,
            EquipoRepository equipoRepository,
            EquipoEstadoService equipoEstadoService) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.equipoRepository = equipoRepository;
        this.equipoEstadoService = equipoEstadoService;
    }

    // RF-11: Programar mantenimiento
    // Entre fecha_inicio y fecha_fin el equipo no se puede reservar.
    // Si no se indica fecha_inicio el mantenimiento empieza ahora.
    @PostMapping
    public ResponseEntity<?> programarMantenimiento(@RequestBody Mantenimiento mantenimiento) {

        if (mantenimiento.getId_equipo() == null) {
            return ResponseEntity.badRequest().body("Debe indicar el equipo");
        }

        if (!equipoRepository.existsById(mantenimiento.getId_equipo())) {
            return ResponseEntity.badRequest().body("El equipo indicado no existe");
        }

        String tipo = mantenimiento.getTipo() == null || mantenimiento.getTipo().isBlank()
                ? "PREVENTIVO"
                : mantenimiento.getTipo().toUpperCase();

        if (!TIPOS.contains(tipo)) {
            return ResponseEntity.badRequest()
                    .body("El tipo debe ser PREVENTIVO o CORRECTIVO");
        }

        if (mantenimiento.getFecha_inicio() == null) {
            mantenimiento.setFecha_inicio(LocalDateTime.now());
        }

        if (mantenimiento.getFecha_fin() != null
                && mantenimiento.getFecha_fin().isBefore(mantenimiento.getFecha_inicio())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de fin no puede ser anterior a la de inicio");
        }

        mantenimiento.setId_mantenimiento(null);
        mantenimiento.setTipo(tipo);
        mantenimiento.setEstado("PROGRAMADO");

        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);

        equipoEstadoService.recalcular(guardado.getId_equipo());

        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarMantenimiento(@PathVariable Integer id) {
        Optional<Mantenimiento> opt = mantenimientoRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Mantenimiento m = opt.get();

        if (ESTADOS_CERRADOS.contains(m.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("El mantenimiento ya está cerrado");
        }

        m.setFecha_fin(LocalDateTime.now());
        m.setEstado("FINALIZADO");

        Mantenimiento guardado = mantenimientoRepository.save(m);

        // El equipo vuelve a estar disponible
        equipoEstadoService.recalcular(guardado.getId_equipo());

        return ResponseEntity.ok(guardado);
    }

    // Actualizar mantenimiento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMantenimiento(@PathVariable Integer id,
            @RequestBody Mantenimiento datos) {

        Optional<Mantenimiento> opt = mantenimientoRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (datos.getTipo() != null && !TIPOS.contains(datos.getTipo().toUpperCase())) {
            return ResponseEntity.badRequest()
                    .body("El tipo debe ser PREVENTIVO o CORRECTIVO");
        }

        if (datos.getId_equipo() != null
                && !equipoRepository.existsById(datos.getId_equipo())) {
            return ResponseEntity.badRequest().body("El equipo indicado no existe");
        }

        Mantenimiento m = opt.get();
        Integer equipoAnterior = m.getId_equipo();

        if (datos.getMotivo() != null) {
            m.setMotivo(datos.getMotivo());
        }

        if (datos.getTipo() != null) {
            m.setTipo(datos.getTipo().toUpperCase());
        }

        if (datos.getId_equipo() != null) {
            m.setId_equipo(datos.getId_equipo());
        }

        if (datos.getFecha_inicio() != null) {
            m.setFecha_inicio(datos.getFecha_inicio());
        }

        if (datos.getFecha_fin() != null) {
            m.setFecha_fin(datos.getFecha_fin());
        }

        if (datos.getEstado() != null) {
            m.setEstado(datos.getEstado().toUpperCase());

            // Al cerrar el mantenimiento se registra cuando termino
            if (ESTADOS_CERRADOS.contains(m.getEstado()) && m.getFecha_fin() == null) {
                m.setFecha_fin(LocalDateTime.now());
            }
        }

        if (m.getFecha_inicio() != null && m.getFecha_fin() != null
                && m.getFecha_fin().isBefore(m.getFecha_inicio())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de fin no puede ser anterior a la de inicio");
        }

        Mantenimiento actualizado = mantenimientoRepository.save(m);

        equipoEstadoService.recalcular(actualizado.getId_equipo());

        if (equipoAnterior != null && !equipoAnterior.equals(actualizado.getId_equipo())) {
            equipoEstadoService.recalcular(equipoAnterior);
        }

        return ResponseEntity.ok(actualizado);
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(estado == null
                ? mantenimientoRepository.findAll()
                : mantenimientoRepository.findByEstado(estado));
    }

    // Eliminar mantenimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMantenimiento(@PathVariable Integer id) {

        Optional<Mantenimiento> opt = mantenimientoRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Integer idEquipo = opt.get().getId_equipo();

        mantenimientoRepository.deleteById(id);

        equipoEstadoService.recalcular(idEquipo);

        return ResponseEntity.ok().build();
    }
}
