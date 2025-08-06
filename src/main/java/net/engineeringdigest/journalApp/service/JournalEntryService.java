package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class JournalEntryService {

    private static final Logger log = LoggerFactory.getLogger(JournalEntryService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    public JournalEntry saveEntry(JournalEntry journalEntry, String userName) {
        try {
            log.info("🔍 SAVE ENTRY DEBUG:");
            log.info("  👤 Username: {}", userName);
            log.info("  📝 Entry title: {}", journalEntry.getTitle());
            log.info("  📄 Entry content length: {}", journalEntry.getContent() != null ? journalEntry.getContent().length() : 0);

            User user = userService.findByUserName(userName);
            if (user == null) {
                log.error("❌ User not found: {}", userName);
                throw new RuntimeException("User not found: " + userName);
            }

            log.info("✅ User found: {}", user.getUserName());
            log.info("  📧 User email: {}", user.getEmail());
            log.info("  📊 Current journal entries count: {}", user.getJournalEntries() != null ? user.getJournalEntries().size() : 0);
            log.info("  🧠 Sentiment analysis enabled: {}", user.isSentimentAnalysis());

            // Set the date
            journalEntry.setDate(LocalDateTime.now());
            log.info("  📅 Set entry date: {}", journalEntry.getDate());

            // Add sentiment analysis if user has it enabled
            if (user.isSentimentAnalysis()) {
                try {
                    // Use the enhanced sentiment analysis with real-time tracking
                    // This complements your existing weekly scheduler
                    Sentiment sentiment = sentimentAnalysisService.analyzeSentimentWithRealTimeTracking(
                            journalEntry.getContent(),
                            user.getEmail(),
                            user.getUserName()
                    );
                    journalEntry.setSentiment(sentiment);
                    log.info("  🧠 Sentiment analysis result: {}", sentiment);
                    log.info("  💬 Sentiment description: {}", sentimentAnalysisService.getSentimentDescription(sentiment));
                } catch (Exception e) {
                    log.warn("⚠️ Sentiment analysis failed: {}", e.getMessage());
                    journalEntry.setSentiment(Sentiment.NEUTRAL);
                }
            } else {
                log.info("  🧠 Sentiment analysis disabled for user");
                journalEntry.setSentiment(null);
            }

            // Save the journal entry first
            log.info("⏳ Saving journal entry to database...");
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            log.info("✅ Journal entry saved with ID: {}", saved.getId());

            // Add to user's journal entries
            if (user.getJournalEntries() == null) {
                user.setJournalEntries(new ArrayList<>());
                log.info("📝 Initialized empty journal entries list for user");
            }

            user.getJournalEntries().add(saved);
            log.info("📝 Added entry to user's list. New count: {}", user.getJournalEntries().size());

            // Save the user
            log.info("⏳ Saving user with updated journal entries...");
            User updatedUser = userRepository.save(user);
            log.info("✅ User saved. Journal entries count: {}", updatedUser.getJournalEntries().size());

            return saved;
        } catch (Exception e) {
            log.error("💥 Error occurred while saving entry for user {}: {}", userName, e.getMessage(), e);
            throw new RuntimeException("Error saving journal entry: " + e.getMessage(), e);
        }
    }

    public JournalEntry saveEntry(JournalEntry journalEntry) {
        try {
            log.info("⏳ SAVE ENTRY (UPDATE MODE) DEBUG:");
            log.info("  🆔 Entry ID: {}", journalEntry.getId());
            log.info("  📝 Title: {}", journalEntry.getTitle());
            log.info("  📄 Content length: {}", journalEntry.getContent() != null ? journalEntry.getContent().length() : 0);
            log.info("  📅 Current date: {}", journalEntry.getDate());
            log.info("  🧠 Current sentiment: {}", journalEntry.getSentiment());

            // For updates, preserve existing sentiment if not recalculating
            if (journalEntry.getDate() == null) {
                journalEntry.setDate(LocalDateTime.now());
                log.info("  📅 Set new date: {}", journalEntry.getDate());
            }

            JournalEntry saved = journalEntryRepository.save(journalEntry);
            log.info("✅ Journal entry updated successfully:");
            log.info("  🆔 Updated ID: {}", saved.getId());
            log.info("  📝 Updated title: {}", saved.getTitle());
            log.info("  📅 Updated date: {}", saved.getDate());

            return saved;
        } catch (Exception e) {
            log.error("💥 Error saving journal entry: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving journal entry: " + e.getMessage(), e);
        }
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        try {
            log.info("🔍 FIND BY ID DEBUG:");
            log.info("  🆔 Looking for entry ID: {}", id);
            log.info("  🔧 ObjectId type: {}", id.getClass().getSimpleName());
            log.info("  📏 ObjectId string length: {}", id.toString().length());

            Optional<JournalEntry> entry = journalEntryRepository.findById(id);
            log.info("  ✅ Entry found: {}", entry.isPresent());

            if (entry.isPresent()) {
                JournalEntry foundEntry = entry.get();
                log.info("  📝 Found entry details:");
                log.info("    🆔 ID: {}", foundEntry.getId());
                log.info("    📝 Title: {}", foundEntry.getTitle());
                log.info("    📅 Date: {}", foundEntry.getDate());
                log.info("    🧠 Sentiment: {}", foundEntry.getSentiment());
                log.info("    📄 Content preview: {}", foundEntry.getContent() != null ?
                        foundEntry.getContent().substring(0, Math.min(50, foundEntry.getContent().length())) + "..." : "null");
            } else {
                log.warn("  ❌ No entry found with ID: {}", id);

                // Additional debugging - let's see what entries exist
                log.info("  🔍 Debugging: Checking all entries in repository...");
                try {
                    journalEntryRepository.findAll().forEach(e -> {
                        log.info("    📝 Existing entry: ID={}, Title={}", e.getId(), e.getTitle());
                    });
                } catch (Exception debugE) {
                    log.error("  💥 Error during debug listing: {}", debugE.getMessage());
                }
            }

            return entry;
        } catch (Exception e) {
            log.error("💥 Error finding journal entry by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public boolean deleteById(ObjectId id, String userName) {
        try {
            log.info("🗑️ DELETE ENTRY DEBUG:");
            log.info("  🆔 Entry ID to delete: {}", id);
            log.info("  👤 Username: {}", userName);
            log.info("  📏 ObjectId string: {}", id.toString());

            User user = userService.findByUserName(userName);
            if (user == null) {
                log.error("❌ User not found: {}", userName);
                return false;
            }

            log.info("✅ User found: {}", user.getUserName());
            log.info("  📊 User's entries count: {}", user.getJournalEntries() != null ? user.getJournalEntries().size() : 0);

            if (user.getJournalEntries() != null) {
                log.info("  🔍 User's entry IDs:");
                user.getJournalEntries().forEach(entry -> {
                    log.info("    📝 Entry ID: {} (equals target: {})",
                            entry.getId(), entry.getId().equals(id));
                });
            }

            // Check if user owns this entry
            boolean userOwnsEntry = user.getJournalEntries() != null &&
                    user.getJournalEntries().stream()
                            .anyMatch(entry -> {
                                boolean matches = entry.getId().equals(id);
                                log.info("    🔍 Checking entry {} against {}: {}", entry.getId(), id, matches);
                                return matches;
                            });

            log.info("  🔐 User owns entry: {}", userOwnsEntry);

            if (!userOwnsEntry) {
                log.error("❌ User {} does not own entry {}", userName, id);
                return false;
            }

            // Remove from user's list
            int beforeSize = user.getJournalEntries().size();
            boolean removed = user.getJournalEntries().removeIf(entry -> {
                boolean shouldRemove = entry.getId().equals(id);
                if (shouldRemove) {
                    log.info("  🗑️ Removing entry: {} - {}", entry.getId(), entry.getTitle());
                }
                return shouldRemove;
            });
            int afterSize = user.getJournalEntries().size();

            log.info("  📝 Removal result: {} entries removed", beforeSize - afterSize);
            log.info("  📊 Size change: {} -> {} entries", beforeSize, afterSize);

            if (!removed || beforeSize == afterSize) {
                log.warn("⚠️ No entries were removed from user's list");
                return false;
            }

            // Save updated user
            User savedUser = userRepository.save(user);
            log.info("✅ User updated in database. New entries count: {}", savedUser.getJournalEntries().size());

            // Delete from journal entries collection
            journalEntryRepository.deleteById(id);
            log.info("✅ Entry deleted from journal collection");

            // Verify deletion
            Optional<JournalEntry> deletedEntry = journalEntryRepository.findById(id);
            log.info("  🔍 Verification - Entry still exists: {}", deletedEntry.isPresent());

            return true;
        } catch (Exception e) {
            log.error("💥 Error deleting journal entry {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
