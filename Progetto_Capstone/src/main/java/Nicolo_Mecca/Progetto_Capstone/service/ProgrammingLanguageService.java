package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.ProgrammingLanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgrammingLanguageService {
    @Autowired
    private ProgrammingLanguageRepository languageRepository;

    public List<ProgrammingLanguage> findAllLanguages() {
        return languageRepository.findAll();
    }

    public ProgrammingLanguage saveLanguage(ProgrammingLanguage language) {
        return languageRepository.save(language);
    }

    public ProgrammingLanguage findByName(String name) {
        return languageRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Programming language not found"));
    }
}