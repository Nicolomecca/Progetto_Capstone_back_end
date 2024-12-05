package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.dto.QuizResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.UserDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.UserProfileDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.UserRankDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.service.QuizService;
import Nicolo_Mecca.Progetto_Capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private QuizService quizService;

    @GetMapping("/leaderboard")
    @PreAuthorize("hasAuthority('USER')")
    public Page<User> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getLeaderboard(page, size);
    }

    @GetMapping("/levels")
    @PreAuthorize("hasAuthority('USER')")
    public Map<String, UserLevel> getUserLevels(@AuthenticationPrincipal User user) {
        return userService.getUserLevels(user);
    }

    @GetMapping("/quiz-history")
    @PreAuthorize("hasAuthority('USER')")
    public Map<String, List<QuizResponseDTO>> getUserQuizHistoryGrouped(@AuthenticationPrincipal User user) {
        return quizService.getUserQuizHistoryGrouped(user);
    }

    @GetMapping("/ranking/{languageName}")
    @PreAuthorize("hasAuthority('USER')")
    public List<UserRankDTO> getUserRankingByLanguage(
            @AuthenticationPrincipal User user,
            @PathVariable String languageName
    ) {
        return userService.getUserRankingByLanguage(languageName);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasAuthority('USER')")
    public String uploadProfileImage(
            @RequestParam("picture") MultipartFile file,
            @AuthenticationPrincipal User user) {
        return userService.uploadImageProfile(file, user);
    }

    @GetMapping("/profile")
    public UserProfileDTO getCurrentUserProfile(@AuthenticationPrincipal User user) {
        return userService.getUserProfile(user);
    }

    @PatchMapping("/upDate-profile")
    public UserProfileDTO updateUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Validated UserDTO userDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        User updatedUser = userService.updateUser(currentUser.getUserId(), userDTO);
        return userService.getUserProfile(updatedUser);
    }

}
