package innovaschools.com.gestion_laboratorio.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import innovaschools.com.gestion_laboratorio.model.Mantenimiento;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {
    List<Mantenimiento> findByEstado(String estado);

    // Mantenimiento sin cerrar cuyo rango incluye el momento indicado
    @Query("""
        SELECT COUNT(m) > 0 FROM Mantenimiento m
        WHERE m.id_equipo = :idEquipo
        AND m.estado NOT IN ('FINALIZADO', 'COMPLETADO', 'CANCELADO')
        AND m.fecha_inicio <= :ahora
        AND (m.fecha_fin IS NULL OR m.fecha_fin >= :ahora)
    """)
    boolean hayMantenimientoVigente(
            @Param("idEquipo") Integer idEquipo,
            @Param("ahora") LocalDateTime ahora
    );

    // Mantenimiento sin cerrar que se cruza con el rango indicado
    @Query("""
        SELECT COUNT(m) > 0 FROM Mantenimiento m
        WHERE m.id_equipo = :idEquipo
        AND m.estado NOT IN ('FINALIZADO', 'COMPLETADO', 'CANCELADO')
        AND m.fecha_inicio < :hasta
        AND (m.fecha_fin IS NULL OR m.fecha_fin > :desde)
    """)
    boolean hayMantenimientoEnRango(
            @Param("idEquipo") Integer idEquipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
