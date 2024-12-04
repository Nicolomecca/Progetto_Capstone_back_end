package Nicolo_Mecca.Progetto_Capstone.enums;


import lombok.Getter;

@Getter
public enum UserLevel {
    BEGINNER(0),
    INTERMEDIATE(80),
    ADVANCED(500),
    EXPERT(100000); // Aggiungi un livello EXPERT se necessario


    private final int requiredScore;

    UserLevel(int requiredScore) {

        this.requiredScore = requiredScore;
    }

    public static UserLevel fromScore(int score) {
        if (score >= EXPERT.requiredScore) return EXPERT; // Controlla prima per il livello più alto
        if (score >= ADVANCED.requiredScore) return ADVANCED;
        if (score >= INTERMEDIATE.requiredScore) return INTERMEDIATE;
        return BEGINNER;
    }

}