package innovaschools.com.gestion_laboratorio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.Equipo;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;
import innovaschools.com.gestion_laboratorio.service.EquipoEstadoService;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoRepository equipoRepository;

    public EquipoController(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @PostMapping
    public ResponseEntity<?> registrarEquipo(@RequestBody Equipo equipo) {

        // Todo equipo nuevo se registra como "Disponible"
        equipo.setId_estado(EquipoEstadoService.DISPONIBLE);

        Equipo nuevoEquipo = equipoRepository.save(equipo);

        return ResponseEntity.ok(nuevoEquipo);
    }

    @GetMapping
public ResponseEntity<?> filtrarEquipos(
        @RequestParam(required = false) Integer idLaboratorio,
        @RequestParam(required = false) Integer idCategoria,
        @RequestParam(required = false) Integer idEstado) {

    List<Equipo> equipos = equipoRepository.filtrarEquipos(
            idLaboratorio,
            idCategoria,
            idEstado
    );

    return ResponseEntity.ok(equipos);
}

    // Actualizar equipo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEquipo(
            @PathVariable Integer id,
            @RequestBody Equipo datos) {

        return equipoRepository.findById(id)
                .map(equipo -> {
                    equipo.setCodigo_inventario(datos.getCodigo_inventario());
                    equipo.setNombre(datos.getNombre());
                    equipo.setModelo(datos.getModelo());
                    equipo.setNumero_serie(datos.getNumero_serie());
                    equipo.setDescripcion(datos.getDescripcion());
                    equipo.setId_categoria(datos.getId_categoria());
                    equipo.setId_laboratorio(datos.getId_laboratorio());
                    equipo.setId_estado(datos.getId_estado());

                    return ResponseEntity.ok(
                            equipoRepository.save(equipo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar equipo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEquipo(@PathVariable Integer id) {

        if (!equipoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        equipoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}