package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Mantenimiento;
import innovaschools.com.gestion_laboratorio.repository.MantenimientoRepository;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoRepository mantenimientoRepository;

    public MantenimientoController(MantenimientoRepository mantenimientoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
    }

    @PostMapping
    public ResponseEntity<?> programarMantenimiento(@RequestBody Mantenimiento mantenimiento) {
        mantenimiento.setFecha_inicio(LocalDateTime.now());
        mantenimiento.setEstado("PROGRAMADO");
        return ResponseEntity.ok(mantenimientoRepository.save(mantenimiento));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarMantenimiento(@PathVariable Integer id) {
        Optional<Mantenimiento> opt = mantenimientoRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Mantenimiento m = opt.get();
        m.setFecha_fin(LocalDateTime.now());
        m.setEstado("FINALIZADO");
        return ResponseEntity.ok(mantenimientoRepository.save(m));
    }

    // RF-08: Actualizar mantenimiento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMantenimiento(@PathVariable Integer id,
            @RequestBody Mantenimiento mantenimientoDetalles) {
        return mantenimientoRepository.findById(id).map(mantenimiento -> {
            mantenimiento.setMotivo(mantenimientoDetalles.getMotivo());
            mantenimiento.setEstado(mantenimientoDetalles.getEstado());
            if (mantenimientoDetalles.getId_equipo() != null) {
                mantenimiento.setId_equipo(mantenimientoDetalles.getId_equipo());
            }
            Mantenimiento actualizado = mantenimientoRepository.save(mantenimiento);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(estado == null
                ? mantenimientoRepository.findAll()
                : mantenimientoRepository.findByEstado(estado));
    }

    // RF-08: Eliminar mantenimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMantenimiento(@PathVariable Integer id) {
        if (!mantenimientoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mantenimientoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}