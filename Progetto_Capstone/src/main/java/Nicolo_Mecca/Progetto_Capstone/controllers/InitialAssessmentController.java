package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.service.InitialAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/assessment")
public class InitialAssessmentController {
    @Autowired
    private InitialAssessmentService initialAssessmentService;

    @GetMapping("/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public List<Map<String, Object>> getInitialAssessment(@PathVariable String languageName) {
        return initialAssessmentService.getInitialAssessment(languageName);
    }

    @PostMapping("/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public InitialAssessment saveInitialAssessment(
            @AuthenticationPrincipal User user,
            @PathVariable String languageName,
            @RequestBody Integer score) {
        return initialAssessmentService.saveInitialAssessment(user, languageName, score);
    }

    @GetMapping("/user/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public Optional<InitialAssessment> getUserInitialAssessment(
            @AuthenticationPrincipal User user,
            @PathVariable String languageName) {
        return initialAssessmentService.getUserInitialAssessment(user, languageName);
    }
}