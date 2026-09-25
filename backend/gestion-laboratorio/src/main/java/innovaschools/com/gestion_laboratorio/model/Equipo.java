package innovaschools.com.gestion_laboratorio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "equipo")
public class Equipo {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id_equipo;

    private String codigo_inventario;
    private String nombre;
    private String modelo;
    private String numero_serie;
    private String descripcion;
    private Integer id_categoria;
    private Integer id_laboratorio;
    private Integer id_estado;

    public Integer getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(Integer id_equipo) {
        this.id_equipo = id_equipo;
    }

    public String getCodigo_inventario() {
        return codigo_inventario;
    }

    public void setCodigo_inventario(String codigo_inventario) {
        this.codigo_inventario = codigo_inventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumero_serie() {
        return numero_serie;
    }

    public void setNumero_serie(String numero_serie) {
        this.numero_serie = numero_serie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id_categoria) {
        this.id_categoria = id_categoria;
    }

    public Integer getId_laboratorio() {
        return id_laboratorio;
    }

    public void setId_laboratorio(Integer id_laboratorio) {
        this.id_laboratorio = id_laboratorio;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }
}