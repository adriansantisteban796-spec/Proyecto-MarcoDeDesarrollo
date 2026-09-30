package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import innovaschools.com.gestion_laboratorio.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByLaboratorioAndFecha(String laboratorio, String fecha);
    List<Reserva> findByEstado(String estado);
}
