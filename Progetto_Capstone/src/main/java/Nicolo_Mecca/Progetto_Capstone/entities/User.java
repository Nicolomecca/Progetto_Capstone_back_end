package Nicolo_Mecca.Progetto_Capstone.entities;

import Nicolo_Mecca.Progetto_Capstone.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"quizResults", "languageProgresses", "initialAssessment"})
public class User implements UserDetails {
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
    @Enumerated(EnumType.STRING)
    private UserRole role;


    @OneToOne(mappedBy = "user")
    private InitialAssessment initialAssessment;

    @OneToMany(mappedBy = "user")
    private List<UserQuizResult> quizResults;

    @OneToMany(mappedBy = "user")
    private List<UserLanguageProgress> languageProgresses;

    public User(String name, String surname, String userName, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.profileImage = "https://ui-avatars.com/api/?name=" +
                name + "+" + surname;
        this.totalScore = 0;
        this.role = UserRole.USER;

    }

    public User(String name, String surname, String userName, String email, String password, UserRole role) {
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.profileImage = "https://ui-avatars.com/api/?name=" + name + "+" + surname;
        this.totalScore = null;  // Admin non ha score
        this.role = role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
}

