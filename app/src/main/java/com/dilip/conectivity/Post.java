package com.dilip.conectivity;
public class Post {
    private String postId;
    private String postImageUrl;
    private String caption;
    private String userId;
    private long timestamp;
    private  String  locationName;

    public Post() {
    }

    public Post(String postId, String postImageUrl, String caption, String userId, long timestamp, String locationName) {
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.caption = caption;
        this.userId = userId;
        this.timestamp = timestamp;
        this.locationName = locationName;
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

    public long getTimestamp() {  // Ensure getter returns 'long'
        return timestamp;
    }

    public void setTimestamp(long timestamp) {  // Ensure setter accepts 'long'
        this.timestamp = timestamp;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}


