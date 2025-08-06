package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SentimentAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisService.class);

    @Autowired(required = false) // Make optional in case Kafka is not configured
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    public Sentiment getSentiment(String content) {
        try {
            log.info("🧠 Analyzing sentiment for content length: {}", content != null ? content.length() : 0);

            if (content == null || content.trim().isEmpty()) {
                return Sentiment.NEUTRAL;
            }

            String lowerContent = content.toLowerCase();

            // Enhanced keyword-based sentiment analysis
            // Positive words
            String[] positiveWords = {
                    "happy", "joy", "love", "excellent", "amazing", "wonderful", "great", "good",
                    "fantastic", "awesome", "brilliant", "perfect", "beautiful", "excited",
                    "thrilled", "delighted", "pleased", "satisfied", "grateful", "blessed",
                    "success", "achievement", "victory", "win", "celebrate", "fun", "enjoy",
                    "smile", "laugh", "cheerful", "optimistic", "positive", "proud", "confident",
                    "peaceful", "relaxed", "content", "hopeful", "inspired", "motivated",
                    "accomplished", "fulfilled", "energetic", "vibrant", "radiant", "blissful"
            };

            // Negative words
            String[] negativeWords = {
                    "sad", "angry", "hate", "terrible", "awful", "horrible", "bad", "worst",
                    "disappointed", "frustrated", "annoyed", "upset", "depressed", "worried",
                    "anxious", "stressed", "tired", "exhausted", "fail", "failure", "problem",
                    "issue", "difficult", "hard", "struggle", "pain", "hurt", "cry", "fear",
                    "lonely", "hopeless", "miserable", "devastated", "furious", "disgusted",
                    "overwhelmed", "confused", "lost", "broken", "defeated", "discouraged",
                    "bitter", "resentful", "jealous", "envious", "regret", "shame", "guilt"
            };

            int positiveScore = 0;
            int negativeScore = 0;

            // Count positive words with context weighting
            for (String word : positiveWords) {
                if (lowerContent.contains(word)) {
                    positiveScore++;
                    // Give extra weight to strong positive words
                    if (word.equals("amazing") || word.equals("fantastic") || word.equals("brilliant")) {
                        positiveScore++;
                    }
                }
            }

            // Count negative words with context weighting
            for (String word : negativeWords) {
                if (lowerContent.contains(word)) {
                    negativeScore++;
                    // Give extra weight to strong negative words
                    if (word.equals("terrible") || word.equals("awful") || word.equals("devastated")) {
                        negativeScore++;
                    }
                }
            }

            log.info("  📊 Sentiment scores - Positive: {}, Negative: {}", positiveScore, negativeScore);

            Sentiment sentiment;
            // Add threshold for more accurate classification
            int threshold = 1;
            if (positiveScore > negativeScore + threshold) {
                sentiment = Sentiment.POSITIVE;
            } else if (negativeScore > positiveScore + threshold) {
                sentiment = Sentiment.NEGATIVE;
            } else {
                sentiment = Sentiment.NEUTRAL;
            }

            log.info("  🎯 Final sentiment: {}", sentiment);
            return sentiment;

        } catch (Exception e) {
            log.error("💥 Error in sentiment analysis: {}", e.getMessage(), e);
            return Sentiment.NEUTRAL;
        }
    }

    /**
     * Send individual sentiment data to Kafka for real-time processing
     * This complements your weekly scheduler analysis
     */
    public void sendSentimentForRealTimeAnalysis(String email, String userName, Sentiment sentiment, String context) {
        try {
            if (kafkaTemplate != null) {
                SentimentData sentimentData = SentimentData.builder()
                        .email(email)
                        .userName(userName)
                        .sentiment("Real-time: " + sentiment.toString() + " - " + context)
                        .timestamp(System.currentTimeMillis())
                        .build();

                log.info("📤 Sending real-time sentiment data to Kafka: {}", sentimentData);
                kafkaTemplate.send("weekly_sentiments", email, sentimentData);
                log.info("✅ Real-time sentiment data sent to Kafka successfully");
            } else {
                log.warn("⚠️ Kafka not configured, skipping real-time sentiment analysis");
            }
        } catch (Exception e) {
            log.error("💥 Error sending real-time sentiment to Kafka: {}", e.getMessage(), e);
        }
    }

    /**
     * Analyze sentiment and optionally send to Kafka for real-time tracking
     * This works alongside your existing weekly scheduler
     */
    public Sentiment analyzeSentimentWithRealTimeTracking(String content, String email, String userName) {
        Sentiment sentiment = getSentiment(content);

        // Send to Kafka for real-time analysis (complements weekly analysis)
        if (sentiment != Sentiment.NEUTRAL) {
            String context = "Journal entry created";
            sendSentimentForRealTimeAnalysis(email, userName, sentiment, context);
        }

        return sentiment;
    }

    /**
     * Get sentiment description for user-friendly display
     */
    public String getSentimentDescription(Sentiment sentiment) {
        if (sentiment == null) {
            return "No sentiment analysis";
        }

        switch (sentiment) {
            case POSITIVE:
                return "😊 Positive - You seem to be in a good mood!";
            case NEGATIVE:
                return "😔 Negative - Take care of yourself, things will get better.";
            case NEUTRAL:
                return "😐 Neutral - A balanced perspective on things.";
            default:
                return "🤔 Unknown sentiment";
        }
    }

    /**
     * Send sentiment data to Kafka for weekly analysis
     * This method is called when we want to queue sentiment data for batch processing
     */
    public void sendSentimentForWeeklyAnalysis(String email, String userName, Sentiment sentiment) {
        try {
            if (kafkaTemplate != null) {
                SentimentData sentimentData = new SentimentData();
                sentimentData.setEmail(email);
                sentimentData.setUserName(userName);
                sentimentData.setSentiment(sentiment.toString());
                sentimentData.setTimestamp(System.currentTimeMillis());

                log.info("📤 Sending sentiment data to Kafka: {}", sentimentData);
                kafkaTemplate.send("weekly_sentiments", sentimentData);
                log.info("✅ Sentiment data sent to Kafka successfully");
            } else {
                log.warn("⚠️ Kafka not configured, skipping weekly sentiment analysis");
            }
        } catch (Exception e) {
            log.error("💥 Error sending sentiment to Kafka: {}", e.getMessage(), e);
        }
    }

    /**
     * Analyze sentiment and optionally send to Kafka for weekly processing
     */
    public Sentiment analyzeSentimentWithWeeklyTracking(String content, String email, String userName) {
        Sentiment sentiment = getSentiment(content);

        // Send to Kafka for weekly analysis (optional)
        if (sentiment != Sentiment.NEUTRAL) {
            sendSentimentForWeeklyAnalysis(email, userName, sentiment);
        }

        return sentiment;
    }
}
