package com.dilip.conectivity;

public class Report {
    private String postId;
    private String reason;
    private long timestamp;
    private String userId;

    // Empty constructor needed for Firebase
    public Report() {
    }

    public Report(String postId, String reason, long timestamp, String userId) {
        this.postId = postId;
        this.reason = reason;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
