package Nicolo_Mecca.Progetto_Capstone.runners;

import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
import Nicolo_Mecca.Progetto_Capstone.service.ProgrammingLanguageService;
import Nicolo_Mecca.Progetto_Capstone.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Component
@Order(1)
public class InizializeDbRunner implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProgrammingLanguageService languageService;
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

        if (languageService.findAllLanguages().isEmpty()) {
            try {
                populateProgrammingLanguages();
                System.out.println("Programming languages populated successfully");
            } catch (Exception e) {
                System.out.println("Error populating programming languages: " + e.getMessage());
            }
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

    private void populateProgrammingLanguages() throws IOException {
        //leggo il file json con getclassloader dalla cartella resources
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("programming_languages.json")) {
            //verifico che il file esiste
            if (inputStream == null) {
                throw new FileNotFoundException("File programming_languages.json not found in resources");
            }
            //converto il json in oggetti Java
            ObjectMapper mapper = new ObjectMapper();
            // creo un albero di nodi
            JsonNode rootNode = mapper.readTree(inputStream);
            // acceddo all'array e itero ogni elemento
            rootNode.get("programming_languages").forEach(lang -> {
                try {
                    String name = lang.get("name").asText();
                    String category = lang.get("category").asText();
                    String icon = lang.get("icon").asText();
                    String theory = lang.get("theory").toString();
                    if (!name.isEmpty() && !category.isEmpty()) {
                        ProgrammingLanguage language = new ProgrammingLanguage(name, category, icon, theory);
                        ProgrammingLanguage savedLanguage = languageService.saveLanguage(language);
                        System.out.println("Added programming language with theory: " + savedLanguage.getName());
                    } else {
                        System.out.println("Skipped language due to empty name or category: " + lang.toString());
                    }
                } catch (Exception e) {
                    System.err.println("Error adding language: " + lang.toString() + ". Error: " + e.getMessage());
                }
            });
        }
        System.out.println("All programming languages populated successfully.");
    }
}
