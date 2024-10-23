package com.dilip.conectivity;

public class Comment {
    private String commentText;
    private String userId;
    private String userName;
    private String userProfileImage; // New field for user profile image
    private long timestamp;
    private String commentId;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String commentText, String userId, String userName, long timestamp, String userProfileImage) {
        this.commentText = commentText;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.userProfileImage  = userProfileImage;
    }

    // Getters and setters
    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}

