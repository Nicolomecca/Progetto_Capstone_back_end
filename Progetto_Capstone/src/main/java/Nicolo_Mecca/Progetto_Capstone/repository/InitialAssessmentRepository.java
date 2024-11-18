package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InitialAssessmentRepository extends JpaRepository<InitialAssessment, UUID> {
    Optional<InitialAssessment> findByUser(User user);

    boolean existsByUser(User user);
}