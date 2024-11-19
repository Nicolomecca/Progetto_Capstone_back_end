package Nicolo_Mecca.Progetto_Capstone.runners;

import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import Nicolo_Mecca.Progetto_Capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class InizializeDbRunner implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcrypt;

    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.surname}")
    private String adminSurname;
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(adminEmail)) {
            createAdmin();
            System.out.println("Admin created successfully with username: " + adminUsername);
        }
    }

    private void createAdmin() {
        UserDTO adminDTO = new UserDTO(
                adminName,
                adminSurname,
                adminUsername,
                adminEmail,
                adminPassword
        );

        User admin = userService.saveAdmin(adminDTO);
        userRepository.save(admin);
    }
}