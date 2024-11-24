package Nicolo_Mecca.Progetto_Capstone.enums;


public enum UserLevel {
    BEGINNER("Bronze", 0),
    INTERMEDIATE("Silver", 70),
    ADVANCED("Gold", 200);

    private final String badge;
    private final int requiredScore;

    UserLevel(String badge, int requiredScore) {
        this.badge = badge;
        this.requiredScore = requiredScore;
    }

    public static UserLevel fromScore(int score) {
        if (score >= ADVANCED.requiredScore) return ADVANCED;
        if (score >= INTERMEDIATE.requiredScore) return INTERMEDIATE;
        return BEGINNER;
    }

    public String getBadge() {
        return badge;
    }

    public int getRequiredScore() {
        return requiredScore;
    }
}