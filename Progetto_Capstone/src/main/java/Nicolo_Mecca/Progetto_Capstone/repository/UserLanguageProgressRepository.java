package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLanguageProgressRepository extends JpaRepository<UserLanguageProgress, UUID> {
    Optional<UserLanguageProgress> findByUserAndProgrammingLanguage(User user, ProgrammingLanguage language);

    List<UserLanguageProgress> findByUser(User user);


}