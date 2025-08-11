package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.enums.Sentiment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SentimentAnalysisService {

    // Positive words/phrases
    private static final List<String> POSITIVE_WORDS = Arrays.asList(
        "happy", "joy", "excited", "wonderful", "amazing", "great", "fantastic", "excellent",
        "love", "adore", "blessed", "grateful", "thankful", "peaceful", "calm", "relaxed",
        "success", "achievement", "accomplished", "proud", "confident", "optimistic",
        "beautiful", "perfect", "awesome", "brilliant", "outstanding", "superb", "marvelous",
        "delighted", "pleased", "satisfied", "content", "fulfilled", "inspired", "motivated"
    );

    // Negative words/phrases
    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
        "sad", "depressed", "angry", "furious", "upset", "disappointed", "frustrated",
        "anxious", "worried", "scared", "afraid", "terrified", "lonely", "isolated",
        "hopeless", "helpless", "defeated", "exhausted", "tired", "stressed", "overwhelmed",
        "hurt", "pain", "suffering", "grief", "loss", "failure", "defeat", "rejection",
        "abandoned", "betrayed", "cheated", "lied", "hate", "despise", "loathe", "disgusted"
    );

    // Neutral words/phrases
    private static final List<String> NEUTRAL_WORDS = Arrays.asList(
        "okay", "fine", "alright", "normal", "usual", "regular", "standard", "average",
        "neutral", "indifferent", "unconcerned", "uninterested", "bored", "tired",
        "routine", "daily", "ordinary", "common", "typical", "expected", "planned"
    );

    public Sentiment analyzeSentiment(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        String lowerContent = content.toLowerCase();
        int positiveScore = 0;
        int negativeScore = 0;
        int neutralScore = 0;

        // Count positive words
        for (String word : POSITIVE_WORDS) {
            if (lowerContent.contains(word)) {
                positiveScore++;
            }
        }

        // Count negative words
        for (String word : NEGATIVE_WORDS) {
            if (lowerContent.contains(word)) {
                negativeScore++;
            }
        }

        // Count neutral words
        for (String word : NEUTRAL_WORDS) {
            if (lowerContent.contains(word)) {
                neutralScore++;
            }
        }

        // Determine sentiment based on scores
        if (positiveScore > negativeScore && positiveScore > neutralScore) {
            return Sentiment.POSITIVE;
        } else if (negativeScore > positiveScore && negativeScore > neutralScore) {
            return Sentiment.NEGATIVE;
        } else {
            return Sentiment.NEUTRAL;
        }
    }

    public Sentiment analyzeSentimentWithEmojis(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        // Check for emojis first
        if (content.contains("😊") || content.contains("😄") || content.contains("😃") || 
            content.contains("😁") || content.contains("😆") || content.contains("😍") ||
            content.contains("🥰") || content.contains("😋") || content.contains("😎") ||
            content.contains("🤗") || content.contains("👍") || content.contains("❤️") ||
            content.contains("💕") || content.contains("💖") || content.contains("💝")) {
            return Sentiment.POSITIVE;
        }

        if (content.contains("😢") || content.contains("😭") || content.contains("😞") ||
            content.contains("😔") || content.contains("😟") || content.contains("😕") ||
            content.contains("😣") || content.contains("😖") || content.contains("😫") ||
            content.contains("😩") || content.contains("😤") || content.contains("😠") ||
            content.contains("😡") || content.contains("🤬") || content.contains("💔") ||
            content.contains("😰") || content.contains("😨") || content.contains("😱")) {
            return Sentiment.NEGATIVE;
        }

        // If no clear emoji indicators, use word-based analysis
        return analyzeSentiment(content);
    }
}
