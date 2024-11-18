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
@Table(name = "programming_languages")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"quizResults", "languageProgresses"}) // Relazioni bidirezionali con mappedBy

public class ProgrammingLanguage {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "programming_language_id")
    private UUID programmingLanguageId;
    @Column(nullable = false, unique = true)
    private String name;
    private String category;

    @OneToMany(mappedBy = "programmingLanguage")
    private List<UserQuizResult> quizResults;

    @OneToMany(mappedBy = "programmingLanguage")
    private List<UserLanguageProgress> languageProgresses;

    public ProgrammingLanguage(String name, String category) {
        this.name = name;
        this.category = category;
    }
}