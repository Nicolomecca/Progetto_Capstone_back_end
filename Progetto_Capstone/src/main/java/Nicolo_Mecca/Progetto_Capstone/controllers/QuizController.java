package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.dto.QuizResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/questions/{languageName}/{difficulty}")
    @PreAuthorize("hasAuthority('USER')")
    public List<Map<String, Object>> getQuizQuestions(
            @PathVariable String languageName,
            @PathVariable String difficulty) {
        // Converte il valore della difficoltà in maiuscolo prima di passarlo all'enum
        QuizDifficulty quizDifficulty;
        try {
            quizDifficulty = QuizDifficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid difficulty level: " + difficulty);
        }

        return quizService.getQuizQuestions(languageName, quizDifficulty);
    }

    @PostMapping("/result")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER')")
    public QuizResponseDTO saveQuizResult(
            @AuthenticationPrincipal User user,
            @RequestBody @Validated QuizResponseDTO quizResult,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(message);
        }

        return quizService.saveQuizResult(user, quizResult);
    }

    @GetMapping("/history/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public List<QuizResponseDTO> getUserQuizHistory(
            @AuthenticationPrincipal User user,
            @PathVariable String languageName) {

        return quizService.getUserQuizHistory(user, languageName);
    }
}