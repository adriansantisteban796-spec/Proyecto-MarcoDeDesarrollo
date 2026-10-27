package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.Equipo;
import innovaschools.com.gestion_laboratorio.model.Reserva;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.repository.MantenimientoRepository;
import innovaschools.com.gestion_laboratorio.repository.ReservaRepository;
import innovaschools.com.gestion_laboratorio.service.EquipoEstadoService;
import innovaschools.com.gestion_laboratorio.service.JwtService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final EquipoRepository equipoRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final JwtService jwtService;

    public ReservaController(
            ReservaRepository reservaRepository,
            EquipoRepository equipoRepository,
            MantenimientoRepository mantenimientoRepository,
            JwtService jwtService) {
        this.reservaRepository = reservaRepository;
        this.equipoRepository = equipoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.jwtService = jwtService;
    }

    // Correo del usuario dueño del token (null si no hay token)
    private String correoDe(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return jwtService.obtenerCorreo(authorization.substring(7));
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
    // Las reservas canceladas no cuentan y se puede ignorar una reserva
    // (la que se esta aprobando o actualizando)
    private boolean horarioOcupado(
            String laboratorio,
            String fecha,
            String horaInicio,
            String horaFin,
            Integer idIgnorar) {

        for (Reserva r :
                reservaRepository.findByLaboratorioAndFecha(laboratorio, fecha)) {

            if (idIgnorar != null && idIgnorar.equals(r.getId_reserva())) {
                continue;
            }

            if (!"CANCELADA".equals(r.getEstado())
                    && horaInicio.compareTo(r.getHoraFin()) < 0
                    && horaFin.compareTo(r.getHoraInicio()) > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    // Devuelve el mensaje de error o null si los datos son validos
    private String validarDatos(Reserva reserva) {

        if (vacio(reserva.getLaboratorio())
                || vacio(reserva.getFecha())
                || vacio(reserva.getHoraInicio())
                || vacio(reserva.getHoraFin())) {
            return "Debe indicar laboratorio, fecha, hora de inicio y hora de fin";
        }

        if (reserva.getHoraInicio().compareTo(reserva.getHoraFin()) >= 0) {
            return "La hora de fin debe ser posterior a la hora de inicio";
        }

        return null;
    }

    // Un equipo bloqueado o con mantenimiento en ese horario no se puede reservar
    // Devuelve el mensaje de error o null si el equipo se puede reservar
    private String validarEquipo(
            Integer idEquipo,
            String fecha,
            String horaInicio,
            String horaFin) {

        if (idEquipo == null) {
            return null;
        }

        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);

        if (equipo == null) {
            return "El equipo indicado no existe";
        }

        if (equipo.getId_estado() != null
                && equipo.getId_estado() == EquipoEstadoService.BLOQUEADO) {
            return "El equipo no está disponible para reservas";
        }

        LocalDateTime desde;
        LocalDateTime hasta;

        try {
            LocalDate dia = LocalDate.parse(fecha);
            desde = dia.atTime(LocalTime.parse(horaInicio));
            hasta = dia.atTime(LocalTime.parse(horaFin));
        } catch (DateTimeParseException e) {
            return "La fecha o la hora tienen un formato inválido";
        }

        if (mantenimientoRepository.hayMantenimientoEnRango(
                idEquipo, desde, hasta)) {
            return "El equipo tiene un mantenimiento programado en ese horario";
        }

        return null;
    }

    // GET: Verificar disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam String laboratorio,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            @RequestParam(required = false) Integer idEquipo) {

        if (horarioOcupado(laboratorio, fecha, horaInicio, horaFin, null)) {
            return ResponseEntity.badRequest()
                    .body("Horario no disponible");
        }

        String errorEquipo = validarEquipo(idEquipo, fecha, horaInicio, horaFin);

        if (errorEquipo != null) {
            return ResponseEntity.badRequest().body(errorEquipo);
        }

        return ResponseEntity.ok("Horario disponible");
    }

    // POST: Registrar solicitud de reserva
    @PostMapping
    public ResponseEntity<?> registrarReserva(
            @RequestBody Reserva reserva,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        String error = validarDatos(reserva);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        if (horarioOcupado(
                reserva.getLaboratorio(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                null)) {

            return ResponseEntity.badRequest()
                    .body("No se puede registrar. El horario ya está ocupado");
        }

        error = validarEquipo(
                reserva.getId_equipo(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin());

        if (error != null) {
            return ResponseEntity.badRequest()
                    .body("No se puede registrar. " + error);
        }

        // Siempre es una reserva nueva, aunque el cuerpo traiga un id
        reserva.setId_reserva(null);
        reserva.setEstado("PENDIENTE");
        reserva.setSolicitante(correoDe(authorization));

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

        // Se vuelve a verificar la disponibilidad antes de aprobar
        if (horarioOcupado(
                reserva.getLaboratorio(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                id)) {

            return ResponseEntity.badRequest()
                    .body("No se puede aprobar. El horario ya está ocupado por otra reserva");
        }

        String error = validarEquipo(
                reserva.getId_equipo(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin());

        if (error != null) {
            return ResponseEntity.badRequest()
                    .body("No se puede aprobar. " + error);
        }

        reserva.setEstado("APROBADA");

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // PUT: Cancelar reserva (solo el solicitante o el Coordinador, y si esta pendiente)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = opt.get();

        if ("CANCELADA".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("La reserva ya está cancelada");
        }

        if (!"PENDIENTE".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden cancelar reservas pendientes");
        }

        String correo = correoDe(authorization);
        boolean esSolicitante =
                correo != null && correo.equals(reserva.getSolicitante());

        if (!esSolicitante && !esCoordinador(authorization)) {
            return ResponseEntity.status(403)
                    .body("Solo el solicitante o el Coordinador pueden cancelar la reserva");
        }

        reserva.setEstado("CANCELADA");

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    // PUT: Actualizar reserva (solo el solicitante o el Coordinador, y si esta pendiente)
    // validando disponibilidad
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(
            @PathVariable Integer id,
            @RequestBody Reserva datos,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        Optional<Reserva> opt = reservaRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = opt.get();

        if (!"PENDIENTE".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden editar reservas pendientes");
        }

        String correo = correoDe(authorization);
        boolean esSolicitante =
                correo != null && correo.equals(reserva.getSolicitante());

        if (!esSolicitante && !esCoordinador(authorization)) {
            return ResponseEntity.status(403)
                    .body("Solo el solicitante o el Coordinador pueden editar la reserva");
        }

        String error = validarDatos(datos);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        // Validar que el nuevo horario no choque con otra reserva
        if (horarioOcupado(
                datos.getLaboratorio(),
                datos.getFecha(),
                datos.getHoraInicio(),
                datos.getHoraFin(),
                id)) {

            return ResponseEntity.badRequest()
                    .body("No se puede actualizar. El horario ya está ocupado");
        }

        error = validarEquipo(
                datos.getId_equipo(),
                datos.getFecha(),
                datos.getHoraInicio(),
                datos.getHoraFin());

        if (error != null) {
            return ResponseEntity.badRequest()
                    .body("No se puede actualizar. " + error);
        }

        reserva.setLaboratorio(datos.getLaboratorio());
        reserva.setFecha(datos.getFecha());
        reserva.setHoraInicio(datos.getHoraInicio());
        reserva.setHoraFin(datos.getHoraFin());
        reserva.setId_equipo(datos.getId_equipo());

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
