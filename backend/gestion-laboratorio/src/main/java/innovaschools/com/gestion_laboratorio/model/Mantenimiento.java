package innovaschools.com.gestion_laboratorio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mantenimiento;

    private Integer id_equipo;
    private String motivo;
    private LocalDateTime fecha_inicio;
    private LocalDateTime fecha_fin;
    private String estado; // PROGRAMADO, FINALIZADO

    public Integer getId_mantenimiento() { return id_mantenimiento; }
    public void setId_mantenimiento(Integer id_mantenimiento) { this.id_mantenimiento = id_mantenimiento; }

    public Integer getId_equipo() { return id_equipo; }
    public void setId_equipo(Integer id_equipo) { this.id_equipo = id_equipo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(LocalDateTime fecha_inicio) { this.fecha_inicio = fecha_inicio; }

    public LocalDateTime getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDateTime fecha_fin) { this.fecha_fin = fecha_fin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}