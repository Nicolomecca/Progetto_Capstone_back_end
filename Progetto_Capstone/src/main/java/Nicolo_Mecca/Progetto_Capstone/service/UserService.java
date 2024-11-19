package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                passwordEncoder.encode(userDTO.password()),
                0
        );
        return userRepository.save(user);
    }

    public void findUserByIdAndDelete(UUID userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

}
