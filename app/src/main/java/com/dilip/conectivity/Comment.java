package com.dilip.conectivity;

public class Comment {
    private String commentId;
    private String postId;
    private String userId;
    private String commentText;

    // Default constructor (required for Firebase)
    public Comment() {}

    // Constructor with parameters
    public Comment(String commentId, String postId, String userId, String commentText) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.commentText = commentText;
    }

    // Getters and setters (if needed)
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
}
