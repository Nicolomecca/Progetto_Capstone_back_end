package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.UserLoginDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.exceptions.UnauthorizedException;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import Nicolo_Mecca.Progetto_Capstone.tools.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private JWT jwt;
    @Autowired
    private PasswordEncoder bcrypt;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public String createToken(UserLoginDTO body) {
        User loggedUser = userRepository.findByUserName(body.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (bcrypt.matches(body.password(), loggedUser.getPassword())) {
            return jwt.createToken(loggedUser);
        }

        throw new UnauthorizedException("Invalid credentials");
    }
}
