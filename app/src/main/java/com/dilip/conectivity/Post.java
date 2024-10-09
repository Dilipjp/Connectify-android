package com.dilip.conectivity;

public class Post {
    private String postId;
    private String userId;
    private String caption;
    private String postImageUrl;
    private int likeCount;
    private boolean isLiked;

    // No-argument constructor for Firebase
    public Post() {}

    // Constructor
    public Post(String postId, String userId, String caption, String postImageUrl, int likeCount, boolean isLiked) {
        this.postId = postId;
        this.userId = userId;
        this.caption = caption;
        this.postImageUrl = postImageUrl;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getPostImageUrl() { return postImageUrl; }
    public void setPostImageUrl(String postImageUrl) { this.postImageUrl = postImageUrl; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}

