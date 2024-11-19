package Nicolo_Mecca.Progetto_Capstone.repository;

import Nicolo_Mecca.Progetto_Capstone.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Page<User> findAllByOrderByTotalScoreDesc(Pageable pageable);
}
