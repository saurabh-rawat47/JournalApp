package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User APIs", description = "Read,Update & Delete")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WeatherService weatherService;

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User userInDb = userService.findByUserName(userName);

            if (userInDb == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Validate input
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
            }

            userInDb.setUserName(user.getUserName().trim());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);

            // Don't return password in response
            userInDb.setPassword(null);
            return new ResponseEntity<>(userInDb, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            User user = userService.findByUserName(userName);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            userRepository.deleteByUserName(userName);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> greeting() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            String greeting = "Hi " + userName;

            try {
                WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
                if (weatherResponse != null && weatherResponse.getCurrent() != null) {
                    greeting += ", Weather feels like " + weatherResponse.getCurrent().getTempC() + "°C";
                }
            } catch (Exception e) {
                // Weather service failed, but don't fail the whole request
                System.out.println("Weather service unavailable: " + e.getMessage());
            }

            return new ResponseEntity<>(greeting, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error getting user greeting: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
