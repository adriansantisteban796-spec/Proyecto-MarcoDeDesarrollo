package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import innovaschools.com.gestion_laboratorio.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Integer> {

    @Query("""
        SELECT e FROM Equipo e
        WHERE (:laboratorio IS NULL OR e.id_laboratorio = :laboratorio)
        AND (:categoria IS NULL OR e.id_categoria = :categoria)
        AND (:estado IS NULL OR e.id_estado = :estado)
    """)
    List<Equipo> filtrarEquipos(
            @Param("laboratorio") Integer laboratorio,
            @Param("categoria") Integer categoria,
            @Param("estado") Integer estado
    );
}

