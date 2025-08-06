package net.engineeringdigest.journalApp.model;

public class SentimentData {
    private String email;
    private String userName;
    private String sentiment;
    private long timestamp;

    // Default constructor
    public SentimentData() {}

    // Constructor with all fields
    public SentimentData(String email, String userName, String sentiment, long timestamp) {
        this.email = email;
        this.userName = userName;
        this.sentiment = sentiment;
        this.timestamp = timestamp;
    }

    // Builder pattern (to match your existing code)
    public static SentimentDataBuilder builder() {
        return new SentimentDataBuilder();
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SentimentData{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    // Builder class to match your existing usage
    public static class SentimentDataBuilder {
        private String email;
        private String userName;
        private String sentiment;
        private long timestamp;

        public SentimentDataBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SentimentDataBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public SentimentDataBuilder sentiment(String sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        public SentimentDataBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SentimentData build() {
            SentimentData sentimentData = new SentimentData();
            sentimentData.email = this.email;
            sentimentData.userName = this.userName;
            sentimentData.sentiment = this.sentiment;
            sentimentData.timestamp = this.timestamp != 0 ? this.timestamp : System.currentTimeMillis();
            return sentimentData;
        }
    }
}
