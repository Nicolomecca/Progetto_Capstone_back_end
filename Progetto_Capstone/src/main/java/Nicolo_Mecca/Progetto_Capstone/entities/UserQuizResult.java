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

}
