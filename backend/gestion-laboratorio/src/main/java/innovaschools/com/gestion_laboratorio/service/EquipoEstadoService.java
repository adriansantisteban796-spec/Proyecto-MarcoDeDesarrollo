package innovaschools.com.gestion_laboratorio.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import innovaschools.com.gestion_laboratorio.model.Equipo;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.repository.IncidenciaRepository;
import innovaschools.com.gestion_laboratorio.repository.MantenimientoRepository;
import innovaschools.com.gestion_laboratorio.repository.PrestamoRepository;

// Mantiene el estado operativo del equipo (tabla estado_equipo) segun
// los prestamos, incidencias y mantenimientos que lo afectan
@Service
public class EquipoEstadoService {

    public static final int DISPONIBLE = 1;
    public static final int EN_PRESTAMO = 2;
    public static final int EN_MANTENIMIENTO = 3;
    public static final int BLOQUEADO = 4;

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final MantenimientoRepository mantenimientoRepository;

    public EquipoEstadoService(
            EquipoRepository equipoRepository,
            PrestamoRepository prestamoRepository,
            IncidenciaRepository incidenciaRepository,
            MantenimientoRepository mantenimientoRepository) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.mantenimientoRepository = mantenimientoRepository;
    }

    // Recalcula el estado del equipo. Prioridad:
    // incidencia critica abierta > mantenimiento vigente > prestamo activo > disponible
    public void recalcular(Integer idEquipo) {

        if (idEquipo == null) {
            return;
        }

        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);

        if (equipo == null) {
            return;
        }

        int estado = DISPONIBLE;

        if (incidenciaRepository.hayCriticaAbierta(idEquipo)) {
            estado = BLOQUEADO;
        } else if (mantenimientoRepository.hayMantenimientoVigente(
                idEquipo, LocalDateTime.now())) {
            estado = EN_MANTENIMIENTO;
        } else if (prestamoRepository.hayPrestamoActivo(idEquipo)) {
            estado = EN_PRESTAMO;
        }

        equipo.setId_estado(estado);
        equipoRepository.save(equipo);
    }
}
