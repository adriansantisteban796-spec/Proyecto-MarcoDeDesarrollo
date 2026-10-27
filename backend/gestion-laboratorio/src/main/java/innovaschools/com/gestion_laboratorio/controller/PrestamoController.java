package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Equipo;
import innovaschools.com.gestion_laboratorio.model.Prestamo;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.repository.PrestamoRepository;
import innovaschools.com.gestion_laboratorio.repository.UsuarioRepository;
import innovaschools.com.gestion_laboratorio.service.EquipoEstadoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoEstadoService equipoEstadoService;

    public PrestamoController(
            PrestamoRepository prestamoRepository,
            EquipoRepository equipoRepository,
            UsuarioRepository usuarioRepository,
            EquipoEstadoService equipoEstadoService) {
        this.prestamoRepository = prestamoRepository;
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipoEstadoService = equipoEstadoService;
    }

    // RF-08: Registrar salida de un equipo
    @PostMapping
    public ResponseEntity<?> registrarPrestamo(@RequestBody Prestamo prestamo) {

        if (prestamo.getId_equipo() == null
                || prestamo.getResponsable() == null
                || prestamo.getResponsable().isBlank()
                || prestamo.getFecha_limite() == null) {
            return ResponseEntity.badRequest()
                    .body("Debe indicar el equipo, el responsable y la fecha límite");
        }

        if (!usuarioRepository.existsById(prestamo.getResponsable())) {
            return ResponseEntity.badRequest()
                    .body("El responsable no está registrado");
        }

        if (!prestamo.getFecha_limite().isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body("La fecha límite debe ser posterior a la fecha actual");
        }

        Equipo equipo = equipoRepository.findById(prestamo.getId_equipo())
                .orElse(null);

        if (equipo == null) {
            return ResponseEntity.badRequest()
                    .body("El equipo indicado no existe");
        }

        // Un equipo prestado, en mantenimiento o bloqueado no se puede asignar
        if (prestamoRepository.hayPrestamoActivo(prestamo.getId_equipo())) {
            return ResponseEntity.status(409)
                    .body("El equipo ya tiene un préstamo activo");
        }

        if (equipo.getId_estado() != null
                && equipo.getId_estado() != EquipoEstadoService.DISPONIBLE) {
            return ResponseEntity.status(409)
                    .body("El equipo no está disponible para préstamo");
        }

        prestamo.setId_prestamo(null);
        prestamo.setFecha_salida(LocalDateTime.now());
        prestamo.setFecha_devolucion(null);
        prestamo.setEstado("ACTIVO");

        Prestamo guardado = prestamoRepository.save(prestamo);

        equipoEstadoService.recalcular(guardado.getId_equipo());

        return ResponseEntity.ok(guardado);
    }

    // RF-09: Registrar devolución
    @PutMapping("/{id}/devolucion")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Integer id) {
        Optional<Prestamo> opt = prestamoRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Prestamo prestamo = opt.get();

        if (!"ACTIVO".equals(prestamo.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden devolver préstamos activos");
        }

        prestamo.setFecha_devolucion(LocalDateTime.now());
        prestamo.setEstado("DEVUELTO");

        Prestamo guardado = prestamoRepository.save(prestamo);

        // El equipo vuelve a estar disponible
        equipoEstadoService.recalcular(guardado.getId_equipo());

        return ResponseEntity.ok(guardado);
    }

    // Cancelar un préstamo activo (el equipo nunca salió)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPrestamo(@PathVariable Integer id) {
        Optional<Prestamo> opt = prestamoRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Prestamo prestamo = opt.get();

        if (!"ACTIVO".equals(prestamo.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden cancelar préstamos activos");
        }

        prestamo.setEstado("CANCELADO");

        Prestamo guardado = prestamoRepository.save(prestamo);

        equipoEstadoService.recalcular(guardado.getId_equipo());

        return ResponseEntity.ok(guardado);
    }

    // Editar un préstamo activo: cambiar el responsable o ampliar la fecha límite
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPrestamo(
            @PathVariable Integer id,
            @RequestBody Prestamo datos) {

        Optional<Prestamo> opt = prestamoRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Prestamo prestamo = opt.get();

        if (!"ACTIVO".equals(prestamo.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden editar préstamos activos");
        }

        if (datos.getResponsable() != null && !datos.getResponsable().isBlank()) {
            if (!usuarioRepository.existsById(datos.getResponsable())) {
                return ResponseEntity.badRequest()
                        .body("El responsable no está registrado");
            }
            prestamo.setResponsable(datos.getResponsable());
        }

        if (datos.getFecha_limite() != null) {
            if (!datos.getFecha_limite().isAfter(LocalDateTime.now())) {
                return ResponseEntity.badRequest()
                        .body("La fecha límite debe ser posterior a la fecha actual");
            }
            prestamo.setFecha_limite(datos.getFecha_limite());
        }

        return ResponseEntity.ok(prestamoRepository.save(prestamo));
    }

    @GetMapping
    public ResponseEntity<?> listarPrestamos(
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(prestamoRepository.filtrarPrestamos(idEquipo, estado));
    }

    // Eliminar préstamo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPrestamo(@PathVariable Integer id) {
        Optional<Prestamo> opt = prestamoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Integer idEquipo = opt.get().getId_equipo();

        prestamoRepository.deleteById(id);

        equipoEstadoService.recalcular(idEquipo);

        return ResponseEntity.ok().build();
    }
}
