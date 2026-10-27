package innovaschools.com.gestion_laboratorio.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import innovaschools.com.gestion_laboratorio.model.Incidencia;
import innovaschools.com.gestion_laboratorio.model.Mantenimiento;
import innovaschools.com.gestion_laboratorio.model.Prestamo;
import innovaschools.com.gestion_laboratorio.model.Reserva;
import innovaschools.com.gestion_laboratorio.repository.*;

// RF-12: solo lectura. No existen endpoints para modificarla.
@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final ReservaRepository reservaRepository;

    public HistorialController(EquipoRepository equipoRepository,
                                PrestamoRepository prestamoRepository,
                                MantenimientoRepository mantenimientoRepository,
                                IncidenciaRepository incidenciaRepository,
                                ReservaRepository reservaRepository) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.reservaRepository = reservaRepository;
    }

    private Map<String, Object> evento(LocalDateTime fecha, String tipo, String detalle) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("fecha", fecha);
        evento.put("tipo", tipo);
        evento.put("detalle", detalle);
        return evento;
    }

    // Fecha y hora de inicio de una reserva (null si el formato no es valido)
    private LocalDateTime inicioReserva(Reserva r) {
        try {
            return LocalDate.parse(r.getFecha())
                    .atTime(LocalTime.parse(r.getHoraInicio()));
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    @GetMapping("/{idEquipo}")
    public ResponseEntity<?> historialEquipo(@PathVariable Integer idEquipo) {

        if (!equipoRepository.existsById(idEquipo)) {
            return ResponseEntity.notFound().build();
        }

        List<Prestamo> prestamos = prestamoRepository.filtrarPrestamos(idEquipo, null);
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> idEquipo.equals(m.getId_equipo())).toList();
        List<Incidencia> incidencias = incidenciaRepository.filtrarIncidencias(idEquipo, null);
        List<Reserva> reservas = reservaRepository.buscarPorEquipo(idEquipo);

        List<Map<String, Object>> eventos = new ArrayList<>();

        for (Prestamo p : prestamos) {
            eventos.add(evento(p.getFecha_salida(), "PRESTAMO_SALIDA",
                    "Préstamo #" + p.getId_prestamo() + " a " + p.getResponsable()));

            if (p.getFecha_devolucion() != null) {
                eventos.add(evento(p.getFecha_devolucion(), "PRESTAMO_DEVOLUCION",
                        "Devolución del préstamo #" + p.getId_prestamo()
                                + " (" + p.getEstado() + ")"));
            }
        }

        for (Incidencia i : incidencias) {
            eventos.add(evento(i.getFecha_reporte(), "INCIDENCIA",
                    "Incidencia #" + i.getId_incidencia() + " [" + i.getSeveridad() + "]: "
                            + i.getDescripcion() + " (" + i.getEstado() + ")"));
        }

        for (Mantenimiento m : mantenimientos) {
            eventos.add(evento(m.getFecha_inicio(), "MANTENIMIENTO_INICIO",
                    "Mantenimiento #" + m.getId_mantenimiento() + " " + m.getTipo()
                            + ": " + m.getMotivo()));

            if (m.getFecha_fin() != null) {
                eventos.add(evento(m.getFecha_fin(), "MANTENIMIENTO_FIN",
                        "Fin del mantenimiento #" + m.getId_mantenimiento()
                                + " (" + m.getEstado() + ")"));
            }
        }

        for (Reserva r : reservas) {
            eventos.add(evento(inicioReserva(r), "RESERVA",
                    "Reserva #" + r.getId_reserva() + " en laboratorio "
                            + r.getLaboratorio() + " (" + r.getEstado() + ")"));
        }

        // Orden cronologico; los eventos sin fecha quedan al final
        eventos.sort(Comparator.comparing(
                e -> (LocalDateTime) e.get("fecha"),
                Comparator.nullsLast(Comparator.naturalOrder())));

        Map<String, Object> historial = new LinkedHashMap<>();
        historial.put("id_equipo", idEquipo);
        historial.put("eventos", eventos);
        historial.put("prestamos", prestamos);
        historial.put("mantenimientos", mantenimientos);
        historial.put("incidencias", incidencias);
        historial.put("reservas", reservas);

        return ResponseEntity.ok(historial);
    }
}
