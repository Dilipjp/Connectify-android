package com.dilip.conectivity;

public class User {
    private String userId;
    private String userName;
    private String userProfileImage;

    // No-argument constructor for Firebase
    public User() {}

    // Constructor for initializing values
    public User(String userId, String userName, String userProfileImage) {
        this.userId = userId;
        setUserName(userName); // Using the setter for validation
        this.userProfileImage = userProfileImage;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        this.userName = userName;
    }

    public String getUserProfileImage() { return userProfileImage; }
    public void setUserProfileImage(String userProfileImage) { this.userProfileImage = userProfileImage; }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfileImage='" + userProfileImage + '\'' +
                '}';
    }
}
