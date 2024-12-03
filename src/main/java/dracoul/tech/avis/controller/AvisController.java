package dracoul.tech.avis.controller;

import dracoul.tech.avis.entity.Avis;
import dracoul.tech.avis.service.AvisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("avis")
@RestController
public class AvisController {

    private final AvisService avisService;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR_READ')")
    @GetMapping("/find/{id}")
    public Avis findById(@PathVariable int id) {
        return this.avisService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void creer(@RequestBody Avis avis) {
        this.avisService.creer(avis);
    }

    @PostMapping(path = "delete/{id}")
    public void delete(@PathVariable int id) {
        avisService.deleteAvis(id);
    }

    @PreAuthorize("hasAnyAuthority({'ADMINISTRATEUR_READ', 'MANAGER_READ'})")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Avis> liste() {
        return this.avisService.liste();
    }
}
