package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.UserProfileDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.UserRankDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import Nicolo_Mecca.Progetto_Capstone.entities.UserQuizResult;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.enums.UserRole;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.UserLanguageProgressRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserQuizResultRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserLanguageProgressRepository progressRepository;
    @Autowired
    private UserQuizResultRepository userQuizResultRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;


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

    public Map<String, UserLevel> getUserLevels(User user) {
        List<UserLanguageProgress> progressList = progressRepository.findByUser(user);
        return progressList.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getProgrammingLanguage().getName(),
                        UserLanguageProgress::getSkillLevel
                ));
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

    public List<UserRankDTO> getUserRankingByLanguage(String languageName) {
        return userQuizResultRepository
                .findByProgrammingLanguageNameAndCompletedTrueOrderByScoreDesc(languageName)
                .stream()
                .collect(Collectors.groupingBy(
                        result -> result.getUser().getUsername(),
                        Collectors.summingInt(UserQuizResult::getScore)
                ))
                .entrySet().stream()
                .map(entry -> new UserRankDTO(
                        entry.getKey(),
                        entry.getValue(),
                        UserLevel.fromScore(entry.getValue()).toString()
                ))
                .sorted(Comparator.comparingInt(UserRankDTO::totalScore).reversed())
                .collect(Collectors.toList());
    }

    public String uploadImageProfile(MultipartFile file, User user) {
        try {
            String url = (String) cloudinaryUploader.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap())
                    .get("url");

            user.setProfileImage(url);
            userRepository.save(user);

            return url;
        } catch (IOException e) {
            throw new BadRequestException("Errore durante l'upload dell'immagine!");
        }
    }

    public UserProfileDTO getUserProfile(User user) {
        List<UserLanguageProgress> progressList = progressRepository.findByUser(user);

        return new UserProfileDTO(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getTotalScore(),
                progressList.stream()
                        .collect(Collectors.toMap(
                                progress -> progress.getProgrammingLanguage().getName(),
                                UserLanguageProgress::getSkillLevel
                        ))
        );
    }
}
