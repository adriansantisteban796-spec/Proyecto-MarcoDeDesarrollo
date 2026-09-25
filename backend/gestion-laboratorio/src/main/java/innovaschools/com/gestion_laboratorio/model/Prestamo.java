package innovaschools.com.gestion_laboratorio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_prestamo;

    private Integer id_equipo;
    private String responsable;
    private LocalDateTime fecha_salida;
    private LocalDateTime fecha_limite;
    private LocalDateTime fecha_devolucion;
    private String estado;

    public Integer getId_prestamo() { return id_prestamo; }
    public void setId_prestamo(Integer id_prestamo) { this.id_prestamo = id_prestamo; }

    public Integer getId_equipo() { return id_equipo; }
    public void setId_equipo(Integer id_equipo) { this.id_equipo = id_equipo; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDateTime getFecha_salida() { return fecha_salida; }
    public void setFecha_salida(LocalDateTime fecha_salida) { this.fecha_salida = fecha_salida; }

    public LocalDateTime getFecha_limite() { return fecha_limite; }
    public void setFecha_limite(LocalDateTime fecha_limite) { this.fecha_limite = fecha_limite; }

    public LocalDateTime getFecha_devolucion() { return fecha_devolucion; }
    public void setFecha_devolucion(LocalDateTime fecha_devolucion) { this.fecha_devolucion = fecha_devolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}