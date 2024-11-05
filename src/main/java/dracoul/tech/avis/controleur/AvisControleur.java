package dracoul.tech.avis.controleur;

import dracoul.tech.avis.entite.Avis;
import dracoul.tech.avis.service.AvisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("avis")
@RestController
public class AvisControleur {

    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void creer(@RequestBody Avis avis) {
        this.avisService.creer(avis);
    }
}
