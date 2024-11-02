package com.dilip.conectivity;

import java.util.List;

public class Post {

    private String postId;
    private String postImageUrl;
    private String caption;
    private String userId;
    private long timestamp;
    private List<String> likes;

    public Post() {
        // Default constructor
    }

    public Post(String postId, String postImageUrl, String caption, String userId, long timestamp) {
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.caption = caption;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

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
}

