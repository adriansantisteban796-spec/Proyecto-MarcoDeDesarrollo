package innovaschools.com.gestion_laboratorio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencia")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_incidencia;

    private Integer id_equipo;
    private String descripcion;
    private String reportado_por;
    private LocalDateTime fecha_reporte;
    private String estado; // ABIERTA, CERRADA

    public Integer getId_incidencia() { return id_incidencia; }
    public void setId_incidencia(Integer id_incidencia) { this.id_incidencia = id_incidencia; }

    public Integer getId_equipo() { return id_equipo; }
    public void setId_equipo(Integer id_equipo) { this.id_equipo = id_equipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getReportado_por() { return reportado_por; }
    public void setReportado_por(String reportado_por) { this.reportado_por = reportado_por; }

    public LocalDateTime getFecha_reporte() { return fecha_reporte; }
    public void setFecha_reporte(LocalDateTime fecha_reporte) { this.fecha_reporte = fecha_reporte; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}