package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    AppCache appCache;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> all = userService.getAll();
            if (all != null && !all.isEmpty()) {
                // Remove passwords from response
                all.forEach(user -> user.setPassword(null));
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Validate input
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
            }

            userService.saveAdmin(user);
            return new ResponseEntity<>("Admin user created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating admin user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/clear-app-cache")
    public ResponseEntity<?> clearAppCache() {
        try {
            appCache.init();
            return new ResponseEntity<>("Cache cleared successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error clearing cache: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
