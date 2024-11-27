package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.QuizQuestion;
import Nicolo_Mecca.Progetto_Capstone.entities.UserQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {
    List<QuizQuestion> findByQuizResult(UserQuizResult quizResult);
}