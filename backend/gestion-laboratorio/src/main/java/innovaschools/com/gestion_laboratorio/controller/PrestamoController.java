package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Prestamo;
import innovaschools.com.gestion_laboratorio.repository.PrestamoRepository;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;

    public PrestamoController(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    // RF-06: Registrar préstamo
    @PostMapping
    public ResponseEntity<?> registrarPrestamo(@RequestBody Prestamo prestamo) {
        prestamo.setFecha_salida(LocalDateTime.now());
        prestamo.setEstado("ACTIVO");
        return ResponseEntity.ok(prestamoRepository.save(prestamo));
    }

    // RF-07: Registrar devolución
    @PutMapping("/{id}/devolucion")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Integer id) {
        Optional<Prestamo> opt = prestamoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Prestamo prestamo = opt.get();
        prestamo.setFecha_devolucion(LocalDateTime.now());
        prestamo.setEstado("DEVUELTO");
        return ResponseEntity.ok(prestamoRepository.save(prestamo));
    }

    @GetMapping
    public ResponseEntity<?> listarPrestamos(
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(prestamoRepository.filtrarPrestamos(idEquipo, estado));
    }
}