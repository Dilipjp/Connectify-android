package com.dilip.conectivity;

public class User {
    private String userId;
    private String userName;
    private String userProfileImage;

    // Constructor
    public User(String userId, String userName, String userProfileImage) {
        this.userId = userId;
        this.userName = userName;
        this.userProfileImage = userProfileImage;

    }

    // Getters and setters
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

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
