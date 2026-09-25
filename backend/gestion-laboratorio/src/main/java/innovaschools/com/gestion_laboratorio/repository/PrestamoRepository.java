package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import innovaschools.com.gestion_laboratorio.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    @Query("""
        SELECT p FROM Prestamo p
        WHERE (:idEquipo IS NULL OR p.id_equipo = :idEquipo)
        AND (:estado IS NULL OR p.estado = :estado)
    """)
    List<Prestamo> filtrarPrestamos(
            @Param("idEquipo") Integer idEquipo,
            @Param("estado") String estado
    );
}