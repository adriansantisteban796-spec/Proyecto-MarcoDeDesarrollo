
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

    public ReservaController(
            ReservaRepository reservaRepository,
            JwtService jwtService) {
        this.reservaRepository = reservaRepository;
        this.jwtService = jwtService;
    }

    // Verificar si el token pertenece a un Coordinador
    private boolean esCoordinador(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return false;
        }

        return "Coordinador".equals(
                jwtService.obtenerRol(authorization.substring(7)));
    }

    // Verificar si el horario esta ocupado
    // Las reservas canceladas no cuentan
    private boolean horarioOcupado(
            String laboratorio,
            String fecha,
            String horaInicio,
            String horaFin) {

        for (Reserva r :
                reservaRepository.findByLaboratorioAndFecha(laboratorio, fecha)) {

            if (!"CANCELADA".equals(r.getEstado())
                    && horaInicio.compareTo(r.getHoraFin()) < 0
                    && horaFin.compareTo(r.getHoraInicio()) > 0) {
                return true;
            }
        }

        return false;
    }

    // GET: Verificar disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam String laboratorio,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {

        if (horarioOcupado(laboratorio, fecha, horaInicio, horaFin)) {
            return ResponseEntity.badRequest()
                    .body("Horario no disponible");
        }

        return ResponseEntity.ok("Horario disponible");
    }

    // POST: Registrar solicitud de reserva
    @PostMapping
    public ResponseEntity<?> registrarReserva(
            @RequestBody Reserva reserva) {

        if (horarioOcupado(
                reserva.getLaboratorio(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin())) {

            return ResponseEntity.badRequest()
                    .body("No se puede registrar. El horario ya está ocupado");
        }

        reserva.setEstado("PENDIENTE");

        return ResponseEntity.status(201)
                .body(reservaRepository.save(reserva));
    }

    // GET: Listar reservas o filtrar por estado
    @GetMapping
    public ResponseEntity<?> listarReservas(
            @RequestParam(required = false) String estado) {

        if (estado == null) {
            return ResponseEntity.ok(reservaRepository.findAll());
        }

        return ResponseEntity.ok(reservaRepository.findByEstado(estado));
    }

    // PUT: Aprobar reserva (solo Coordinador)
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (!esCoordinador(authorization)) {
            return ResponseEntity.status(403)
                    .body("Solo el Coordinador puede aprobar reservas");
        }

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = opt.get();

        if (!"PENDIENTE".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden aprobar reservas pendientes");
        }

        reserva.setEstado("APROBADA");

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // PUT: Cancelar reserva
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Integer id) {

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = opt.get();

        if ("CANCELADA".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("La reserva ya está cancelada");
        }

        reserva.setEstado("CANCELADA");

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // PUT: Actualizar reserva validando disponibilidad
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(
            @PathVariable Integer id,
            @RequestBody Reserva datos) {

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = opt.get();

        // Validar que el nuevo horario no choque con otra reserva
        for (Reserva otra :
                reservaRepository.findByLaboratorioAndFecha(
                        datos.getLaboratorio(), datos.getFecha())) {

            if (!otra.getId_reserva().equals(id)
                    && !"CANCELADA".equals(otra.getEstado())
                    && datos.getHoraInicio().compareTo(otra.getHoraFin()) < 0
                    && datos.getHoraFin().compareTo(otra.getHoraInicio()) > 0) {

                return ResponseEntity.badRequest()
                        .body("No se puede actualizar. El horario ya está ocupado");
            }
        }

        reserva.setLaboratorio(datos.getLaboratorio());
        reserva.setFecha(datos.getFecha());
        reserva.setHoraInicio(datos.getHoraInicio());
        reserva.setHoraFin(datos.getHoraFin());

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // DELETE: Eliminar reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(
            @PathVariable Integer id) {

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        reservaRepository.delete(opt.get());

        return ResponseEntity.noContent().build();
    }
}
