package com.dilip.conectivity;

public class Post {

    private String postId;
    private String postImageUrl;
    private String caption;
    private String userId;
    private long timestamp;
    private int likeCount;
    private boolean isLiked;

    // Default constructor (needed for Firebase)
    public Post() {
        // Default constructor
    }

    // Parameterized constructor
    public Post(String postId, String postImageUrl, String caption, String userId, long timestamp, int likeCount, boolean isLiked) {
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.caption = caption;
        this.userId = userId;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.isLiked = isLiked;  // Initialize based on incoming data
    }

    // Getters and Setters
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        this.isLiked = liked;
    }
}
