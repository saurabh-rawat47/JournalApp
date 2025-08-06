package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(JournalEntryController.class);

    @GetMapping
    @Operation(summary = "Get all journal entries of user")
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("🔍 GET ALL ENTRIES DEBUG:");
            log.info("  👤 Username: {}", userName);

            User user = userService.findByUserName(userName);

            if (user == null) {
                log.error("❌ User not found: {}", userName);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            log.info("✅ User found: {}", user.getUserName());

            List<JournalEntry> all = user.getJournalEntries();
            log.info("📊 Journal entries from user object:");
            log.info("  📊 Entries list is null: {}", all == null);
            log.info("  📊 Entries count: {}", all != null ? all.size() : 0);

            if (all != null && !all.isEmpty()) {
                log.info("📝 Entry details:");
                for (int i = 0; i < all.size(); i++) {
                    JournalEntry entry = all.get(i);
                    log.info("  Entry {}: ID={}, Title={}, Date={}",
                            i + 1, entry.getId(), entry.getTitle(), entry.getDate());
                }
                return new ResponseEntity<>(all, HttpStatus.OK);
            }

            log.info("📝 No entries found, returning empty list");
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("💥 Error fetching entries for user {}: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    e.getMessage(), e);
            return new ResponseEntity<>("Error fetching entries: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "Create entry")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("🔍 CREATE ENTRY DEBUG:");
            log.info("  👤 User: {}", userName);
            log.info("  📝 Title: {}", myEntry.getTitle());
            log.info("  📄 Content length: {}", myEntry.getContent() != null ? myEntry.getContent().length() : 0);
            log.info("  📦 Full entry: {}", myEntry);

            // Validate input
            if (myEntry.getTitle() == null || myEntry.getTitle().trim().isEmpty()) {
                log.error("❌ Validation failed: Title is empty");
                return new ResponseEntity<>("Title is required", HttpStatus.BAD_REQUEST);
            }
            if (myEntry.getContent() == null || myEntry.getContent().trim().isEmpty()) {
                log.error("❌ Validation failed: Content is empty");
                return new ResponseEntity<>("Content is required", HttpStatus.BAD_REQUEST);
            }

            log.info("⏳ Calling journalEntryService.saveEntry...");
            JournalEntry savedEntry = journalEntryService.saveEntry(myEntry, userName);
            log.info("✅ Entry saved successfully: {}", savedEntry);
            log.info("  🆔 Saved entry ID: {}", savedEntry.getId());
            log.info("  📅 Saved entry date: {}", savedEntry.getDate());

            return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("💥 Error creating entry for user {}: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    e.getMessage(), e);
            return new ResponseEntity<>("Error creating entry: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    @Operation(summary = "Get journal entry by id")
    public ResponseEntity<?> getJournalEntryById(@PathVariable String myId) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Check if user owns this entry
            List<JournalEntry> userEntries = user.getJournalEntries().stream()
                    .filter(x -> x.getId().equals(objectId))
                    .toList();

            if (!userEntries.isEmpty()) {
                Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
                if (journalEntry.isPresent()) {
                    return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Journal entry not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid entry ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("id/{myId}")
    @Operation(summary = "Delete journal entry by id")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            boolean removed = journalEntryService.deleteById(objectId, userName);
            if (removed) {
                return new ResponseEntity<>("Entry deleted successfully", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Entry not found or not authorized", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid entry ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("id/{myId}")
    @Operation(summary = "Update journal entry by id")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable String myId, @RequestBody JournalEntry newEntry) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Validate input
            if (newEntry.getTitle() == null || newEntry.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>("Title is required", HttpStatus.BAD_REQUEST);
            }
            if (newEntry.getContent() == null || newEntry.getContent().trim().isEmpty()) {
                return new ResponseEntity<>("Content is required", HttpStatus.BAD_REQUEST);
            }

            // Check if user owns this entry
            List<JournalEntry> collect = user.getJournalEntries().stream()
                    .filter(x -> x.getId().equals(objectId))
                    .collect(Collectors.toList());

            if (!collect.isEmpty()) {
                Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
                if (journalEntry.isPresent()) {
                    JournalEntry old = journalEntry.get();
                    old.setTitle(newEntry.getTitle().trim());
                    old.setContent(newEntry.getContent().trim());
                    JournalEntry updatedEntry = journalEntryService.saveEntry(old);
                    return new ResponseEntity<>(updatedEntry, HttpStatus.OK);
                }
            }

            return new ResponseEntity<>("Entry not found or not authorized", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid entry ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
