package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.dto.InitialAssessmentDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.InitialAssessmentResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.service.InitialAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/user/completed")
    @PreAuthorize("hasAuthority('USER')")
    public Map<String, Boolean> checkUserAssessmentCompletion(@AuthenticationPrincipal User user) {
        boolean completed = initialAssessmentService.hasUserCompletedAnyAssessment(user);
        return Map.of("completed", completed);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER')")
    public InitialAssessmentResponseDTO saveInitialAssessment(
            @AuthenticationPrincipal User user,
            @RequestBody @Validated InitialAssessmentDTO body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(message);
        }

        return initialAssessmentService.saveInitialAssessment(user, body);
    }

    @GetMapping("/user/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public Optional<InitialAssessment> getUserInitialAssessment(
            @AuthenticationPrincipal User user,
            @PathVariable String languageName) {
        return initialAssessmentService.getUserInitialAssessment(user, languageName);
    }
}