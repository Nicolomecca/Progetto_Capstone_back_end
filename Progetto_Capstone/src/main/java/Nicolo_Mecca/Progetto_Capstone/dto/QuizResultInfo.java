package Nicolo_Mecca.Progetto_Capstone.dto;

import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;

public record QuizResultInfo(
        String languageName,
        Integer score,
        QuizDifficulty difficulty
) {
}