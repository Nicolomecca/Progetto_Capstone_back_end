package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.service.PerplexityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/answer")
public class ExplanationController {
    private final PerplexityService perplexityService;

    public ExplanationController(PerplexityService perplexityService) {
        this.perplexityService = perplexityService;
    }

    @PostMapping("/explanation")
    @PreAuthorize("hasAuthority('USER')")
    public Map<String, String> getExplanation(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String explanation = perplexityService.getExplanation(
                    request.get("question"),
                    request.get("userAnswer"),
                    request.get("correctAnswer")
            );
            response.put("explanation", explanation);
        } catch (Exception e) {
            response.put("error", "Errore durante la generazione della spiegazione: " + e.getMessage());
        }
        return response;
    }
}