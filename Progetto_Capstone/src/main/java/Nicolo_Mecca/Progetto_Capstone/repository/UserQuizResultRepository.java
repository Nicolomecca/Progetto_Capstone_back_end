package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserQuizResultRepository extends JpaRepository<UserQuizResult, UUID> {
    List<UserQuizResult> findByUserAndProgrammingLanguage(User user, ProgrammingLanguage language);

    List<UserQuizResult> findByUser(User user);

    List<UserQuizResult> findByProgrammingLanguageNameAndCompletedTrueOrderByScoreDesc(String languageName);

}
