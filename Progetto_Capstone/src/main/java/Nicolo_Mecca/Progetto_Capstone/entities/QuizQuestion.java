package Nicolo_Mecca.Progetto_Capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
public class QuizQuestion {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "quiz_question_id")
    private UUID quizQuestionId;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @ElementCollection
    @CollectionTable(
            name = "quiz_question_answers",
            joinColumns = @JoinColumn(name = "quiz_question_id")
    )
    @MapKeyColumn(name = "answer_key")
    @Column(name = "answer_value")
    private Map<String, String> answers;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "user_quiz_result_id")
    private UserQuizResult quizResult;

    public QuizQuestion(String questionText, Map<String, String> answers, String userAnswer, String correctAnswer) {
        this.questionText = questionText;
        this.answers = answers;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
    }
}