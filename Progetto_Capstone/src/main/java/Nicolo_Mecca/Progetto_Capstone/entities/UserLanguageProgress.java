package Nicolo_Mecca.Progetto_Capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_language_progress")
@Data
@NoArgsConstructor
public class UserLanguageProgress {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "user_language_progress_id")
    private UUID userLanguageProgressId;
    @Enumerated(EnumType.STRING)
    private UserLevel skillLevel;
    @Column(name = "current_score")
    private Integer currentScore;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "programming_language_id")
    private ProgrammingLanguage programmingLanguage;

    public UserLanguageProgress(UserLevel skillLevel, Integer currentScore) {
        this.skillLevel = skillLevel;
        this.currentScore = currentScore;
    }
}
