package innovaschools.com.gestion_laboratorio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import innovaschools.com.gestion_laboratorio.model.Mantenimiento;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {
    List<Mantenimiento> findByEstado(String estado);
}