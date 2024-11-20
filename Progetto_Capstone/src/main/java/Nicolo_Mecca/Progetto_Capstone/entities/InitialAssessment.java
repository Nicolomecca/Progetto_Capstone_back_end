package Nicolo_Mecca.Progetto_Capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "initial_assessments")
@Data
@NoArgsConstructor
public class InitialAssessment {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "initial_assessment_id")
    private UUID initial_assessmentId;
    @Column(name = "score")
    private Integer score;
    @Column(name = "date")
    private LocalDateTime date;
    private Boolean completed;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "programming_language_id")
    private ProgrammingLanguage programmingLanguage;

    public InitialAssessment(Integer score, LocalDateTime date, Boolean completed) {
        this.score = score;
        this.date = date;
        this.completed = completed;
    }

}
