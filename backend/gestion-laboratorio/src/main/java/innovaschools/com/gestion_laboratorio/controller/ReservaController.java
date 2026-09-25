package innovaschools.com.gestion_laboratorio.controller;

import innovaschools.com.gestion_laboratorio.model.Reserva;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private List<Reserva> reservas = new ArrayList<>();

    @GetMapping("/disponibilidad")
    public String verificarDisponibilidad(
            @RequestParam String laboratorio,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {

        for (Reserva reserva : reservas) {

            if (reserva.getLaboratorio().equals(laboratorio)
                    && reserva.getFecha().equals(fecha)
                    && horaInicio.compareTo(reserva.getHoraFin()) < 0
                    && horaFin.compareTo(reserva.getHoraInicio()) > 0) {

                return "Horario no disponible";
            }
        }

        return "Horario disponible";
    }

    @PostMapping
    public String registrarReserva(@RequestBody Reserva reserva) {

        for (Reserva r : reservas) {

            if (r.getLaboratorio().equals(reserva.getLaboratorio())
                    && r.getFecha().equals(reserva.getFecha())
                    && reserva.getHoraInicio().compareTo(r.getHoraFin()) < 0
                    && reserva.getHoraFin().compareTo(r.getHoraInicio()) > 0) {

                return "No se puede registrar. El horario ya está ocupado";
            }
        }

        reservas.add(reserva);

        return "Solicitud de reserva registrada correctamente";
    }
}
