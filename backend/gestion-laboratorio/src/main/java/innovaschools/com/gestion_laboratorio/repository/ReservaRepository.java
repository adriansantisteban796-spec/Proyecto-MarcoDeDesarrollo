package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import innovaschools.com.gestion_laboratorio.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByLaboratorioAndFecha(String laboratorio, String fecha);
    List<Reserva> findByEstado(String estado);

    @Query("SELECT r FROM Reserva r WHERE r.id_equipo = :idEquipo")
    List<Reserva> buscarPorEquipo(@Param("idEquipo") Integer idEquipo);
}
