package Nicolo_Mecca.Progetto_Capstone.entities;

import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_quiz_results")
@Data
@NoArgsConstructor
public class UserQuizResult {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "user_quiz_result_id")
    private UUID userQuizResultId;
    @Enumerated(EnumType.STRING)
    private QuizDifficulty difficulty;
    private Integer score;
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "programming_language_id")
    private ProgrammingLanguage programmingLanguage;

    public UserQuizResult(QuizDifficulty difficulty, Integer score, LocalDateTime completionDate, Boolean completed) {
        this.difficulty = difficulty;
        this.score = score;
        this.completionDate = completionDate;
        this.completed = completed;
    }

    // Definizione dell'enum UserLevel
    public enum UserLevel {
        // Definizione delle costanti dell'enum con due parametri:
        // 1. nome del badge (come mostrato nell'immagine: Bronze, Silver, Gold)
        // 2. punteggio minimo richiesto per ottenere quel livello
        BEGINNER("Bronze", 0),      // Livello iniziale, badge Bronze
        INTERMEDIATE("Silver", 100), // Livello medio, badge Silver, richiede 100 punti
        VETERAN("Gold", 200);       // Livello massimo, badge Gold, richiede 200 punti

        // Campi privati dell'enum
        private final String badge;        // Nome del badge
        private final int requiredScore;   // Punteggio richiesto

        // Costruttore dell'enum che viene chiamato per ogni costante
        UserLevel(String badge, int requiredScore) {
            this.badge = badge;
            this.requiredScore = requiredScore;
        }

        // Metodo statico che determina il livello in base al punteggio
        public static UserLevel fromScore(int score) {
            if (score >= VETERAN.requiredScore) return VETERAN;      // Se score ≥ 200 → Gold
            if (score >= INTERMEDIATE.requiredScore) return INTERMEDIATE; // Se score ≥ 100 → Silver
            return BEGINNER;    // Altrimenti → Bronze
        }

        // Getter per ottenere il nome del badge
        public String getBadge() {
            return badge;
        }

        // Getter per ottenere il punteggio richiesto
        public int getRequiredScore() {
            return requiredScore;
        }
    }
}
