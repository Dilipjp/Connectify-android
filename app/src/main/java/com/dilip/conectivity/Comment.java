package com.dilip.conectivity;

public class Comment {
    private String commentId; // Optional, if you want to keep track of comment IDs
    private String postId;     // Optional, you may want to associate it with the post
    private String userId;     // User ID of the commenter
    private String commentText; // The actual comment text
    private long timestamp;     // Timestamp for the comment

    // Default constructor (required for Firebase)
    public Comment() {}

    // Constructor for posting comments
    public Comment(String userId, String commentText) {
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = System.currentTimeMillis(); // Set current timestamp
    }

    // Full constructor
    public Comment(String commentId, String postId, String userId, String commentText, long timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
