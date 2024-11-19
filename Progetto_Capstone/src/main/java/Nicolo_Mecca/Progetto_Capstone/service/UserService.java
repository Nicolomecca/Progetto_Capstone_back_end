package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.enums.UserRole;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id" + userId + "not found"));
    }

    public User saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.email()))
            throw new BadRequestException("Email already in use");
        if (userRepository.existsByUserName(userDTO.userName()))
            throw new BadRequestException("Username already in use");
        User user = new User(
                userDTO.name(),
                userDTO.surname(),
                userDTO.userName(),
                userDTO.email(),
                passwordEncoder.encode(userDTO.password())
        );
        return userRepository.save(user);
    }

    public User saveAdmin(UserDTO adminDTO) {
        if (userRepository.existsByEmail(adminDTO.email()))
            throw new BadRequestException("Email already in use");
        if (userRepository.existsByUserName(adminDTO.userName()))
            throw new BadRequestException("Username already in use");

        // Usa il costruttore specifico per admin
        User admin = new User(
                adminDTO.name(),
                adminDTO.surname(),
                adminDTO.userName(),
                adminDTO.email(),
                passwordEncoder.encode(adminDTO.password()),
                UserRole.ADMIN
        );

        return userRepository.save(admin);
    }

    public void findUserByIdAndDelete(UUID userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    public Page<User> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }

    public Page<User> getLeaderboard(int page, int size) {
        return userRepository.findAllByOrderByTotalScoreDesc(
                PageRequest.of(page, size)
        );
    }

    public User getProfile(UUID userId) {
        User user = findById(userId);
        // Aggiorna il livello basato sul punteggio totale
        UserLevel currentLevel = UserLevel.fromScore(user.getTotalScore());
        user.setSkillLevel(currentLevel);

        return user;
    }


}
