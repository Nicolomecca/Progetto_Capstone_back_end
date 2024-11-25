package Nicolo_Mecca.Progetto_Capstone.enums;


import lombok.Getter;

@Getter
public enum UserLevel {
    BEGINNER("Bronze", 0),
    INTERMEDIATE("Silver", 80),
    ADVANCED("Gold", 500),
    EXPERT("Platinum", 100000); // Aggiungi un livello EXPERT se necessario

    private final String badge;
    private final int requiredScore;

    UserLevel(String badge, int requiredScore) {
        this.badge = badge;
        this.requiredScore = requiredScore;
    }

    public static UserLevel fromScore(int score) {
        if (score >= EXPERT.requiredScore) return EXPERT; // Controlla prima per il livello più alto
        if (score >= ADVANCED.requiredScore) return ADVANCED;
        if (score >= INTERMEDIATE.requiredScore) return INTERMEDIATE;
        return BEGINNER;
    }

}