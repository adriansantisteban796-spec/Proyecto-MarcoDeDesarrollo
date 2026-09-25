package innovaschools.com.gestion_laboratorio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innovaschools.com.gestion_laboratorio.model.Equipo;
import innovaschools.com.gestion_laboratorio.repository.EquipoRepository;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoRepository equipoRepository;

    public EquipoController(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @PostMapping
    public ResponseEntity<?> registrarEquipo(@RequestBody Equipo equipo) {

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
}