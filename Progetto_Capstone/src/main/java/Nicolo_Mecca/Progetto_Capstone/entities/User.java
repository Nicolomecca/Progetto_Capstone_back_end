package Nicolo_Mecca.Progetto_Capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"quizResults", "languageProgresses", "initialAssessment"})
public class User {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id")
    private UUID userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "profile_image")
    private String profileImage;
    @Column(name = "total_score")
    private Integer totalScore;

    @OneToOne(mappedBy = "user")
    private InitialAssessment initialAssessment;

    @OneToMany(mappedBy = "user")
    private List<UserQuizResult> quizResults;

    @OneToMany(mappedBy = "user")
    private List<UserLanguageProgress> languageProgresses;

    public User(String name, String surname, String userName, String email, String password, Integer totalScore) {
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.profileImage = "https://ui-avatars.com/api/?name=" +
                name + "+" + surname;
        this.totalScore = totalScore;
    }
}
