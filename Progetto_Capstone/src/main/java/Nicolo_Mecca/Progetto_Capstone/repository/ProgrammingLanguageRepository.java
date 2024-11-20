package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProgrammingLanguageRepository extends JpaRepository<ProgrammingLanguage, UUID> {
    Optional<ProgrammingLanguage> findByName(String name);

    boolean existsByName(String name);
}
