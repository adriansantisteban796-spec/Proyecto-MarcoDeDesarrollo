package innovaschools.com.gestion_laboratorio.controller;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Reserva;
import innovaschools.com.gestion_laboratorio.repository.ReservaRepository;
import innovaschools.com.gestion_laboratorio.service.JwtService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final JwtService jwtService;

    public ReservaController(ReservaRepository reservaRepository, JwtService jwtService) {
        this.reservaRepository = reservaRepository;
        this.jwtService = jwtService;
    }

    // Revisa que el token del encabezado Authorization sea de un Coordinador
    private boolean esCoordinador(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return false;
        }

        return "Coordinador".equals(jwtService.obtenerRol(authorization.substring(7)));
    }

    // Revisa si el horario choca con otra reserva (las canceladas no cuentan)
    private boolean horarioOcupado(String laboratorio, String fecha, String horaInicio, String horaFin) {

        for (Reserva r : reservaRepository.findByLaboratorioAndFecha(laboratorio, fecha)) {

            if (!r.getEstado().equals("CANCELADA")
                    && horaInicio.compareTo(r.getHoraFin()) < 0
                    && horaFin.compareTo(r.getHoraInicio()) > 0) {

                return true;
            }
        }

        return false;
    }

    @GetMapping("/disponibilidad")
    public String verificarDisponibilidad(
            @RequestParam String laboratorio,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {

        if (horarioOcupado(laboratorio, fecha, horaInicio, horaFin)) {
            return "Horario no disponible";
        }

        return "Horario disponible";
    }

    // Registrar solicitud de reserva
    @PostMapping
    public ResponseEntity<?> registrarReserva(@RequestBody Reserva reserva) {

        if (horarioOcupado(reserva.getLaboratorio(), reserva.getFecha(),
                reserva.getHoraInicio(), reserva.getHoraFin())) {
            return ResponseEntity.badRequest()
                    .body("No se puede registrar. El horario ya está ocupado");
        }

        reserva.setEstado("PENDIENTE");
        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // Listar reservas 
    @GetMapping
    public ResponseEntity<?> listarReservas(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(estado == null
                ? reservaRepository.findAll()
                : reservaRepository.findByEstado(estado));
    }

    // Aprobar reserva
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (!esCoordinador(authorization)) {
            return ResponseEntity.status(403)
                    .body("Solo el Coordinador puede aprobar reservas");
        }

        Optional<Reserva> opt = reservaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Reserva reserva = opt.get();
        if (!reserva.getEstado().equals("PENDIENTE")) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden aprobar reservas pendientes");
        }

        reserva.setEstado("APROBADA");
        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // Cancelar reserva
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Integer id) {
        Optional<Reserva> opt = reservaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Reserva reserva = opt.get();
        if (reserva.getEstado().equals("CANCELADA")) {
            return ResponseEntity.badRequest()
                    .body("La reserva ya está cancelada");
        }

        reserva.setEstado("CANCELADA");
        return ResponseEntity.ok(reservaRepository.save(reserva));
    }
}
