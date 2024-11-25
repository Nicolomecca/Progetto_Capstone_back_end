package Nicolo_Mecca.Progetto_Capstone.controllers;

import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

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

}
