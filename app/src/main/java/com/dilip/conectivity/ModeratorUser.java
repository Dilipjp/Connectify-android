package com.dilip.conectivity;

public class ModeratorUser {
    private String userId;
    private String userName;
    private String userProfileImage;
    private  String userStatus;

    public ModeratorUser() {
        // Default constructor required for Firebase
    }

    public ModeratorUser(String userId, String userName, String userProfileImage, String userStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userProfileImage = userProfileImage;
        this.userStatus = userStatus;
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

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
