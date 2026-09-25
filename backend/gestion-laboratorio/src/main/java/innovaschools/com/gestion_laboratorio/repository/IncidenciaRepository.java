package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import innovaschools.com.gestion_laboratorio.model.Incidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {

    @Query("""
        SELECT i FROM Incidencia i
        WHERE (:idEquipo IS NULL OR i.id_equipo = :idEquipo)
        AND (:estado IS NULL OR i.estado = :estado)
    """)
    List<Incidencia> filtrarIncidencias(
            @Param("idEquipo") Integer idEquipo,
            @Param("estado") String estado
    );
}