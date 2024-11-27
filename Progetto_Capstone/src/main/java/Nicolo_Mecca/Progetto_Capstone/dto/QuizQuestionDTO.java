package Nicolo_Mecca.Progetto_Capstone.dto;

import java.util.Map;

public record QuizQuestionDTO(
        String questionText,
        Map<String, String> answers,
        String userAnswer,
        String correctAnswer
) {
}