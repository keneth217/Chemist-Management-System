package com.chemist.system.tenant.controller.chemist;

import com.chemist.system.tenant.dto.ChemistProfileDTO;
import com.chemist.system.tenant.model.Chemist;
import com.chemist.system.tenant.service.ChemistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chemist")
public class ChemistController {

    private final ChemistService service;

    public ChemistController(ChemistService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<String> exampleEndpoint() {
        return ResponseEntity.ok("Hello from ChemistController!");
    }

    @GetMapping("/all-chemist")
    public ResponseEntity<String> allChemist() {
        return ResponseEntity.ok("Hello from ChemistController!");
    }


    @PostMapping("/create")
    public ResponseEntity<Chemist> createChemist(ChemistProfileDTO profileDTO) {
        Chemist savedChemist = service.saveProfile(profileDTO);
        return ResponseEntity.ok(savedChemist);
    }


}
