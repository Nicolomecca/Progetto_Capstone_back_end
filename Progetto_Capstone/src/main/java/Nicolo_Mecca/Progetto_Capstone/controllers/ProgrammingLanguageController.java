package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.service.ProgrammingLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/languages")
public class ProgrammingLanguageController {
    @Autowired
    private ProgrammingLanguageService languageService;


    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<ProgrammingLanguage> getAvailableLanguages() {
        return languageService.findAllLanguages();
    }
}
