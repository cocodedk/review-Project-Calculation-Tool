package com.aljamour.pkveksamen.Model;

public class User {
    private long userID;
    private String userName;
    private String email;
    private String userPassword;
    private UserRole role;

    public User(){
    }

    public User(String userName, String email, String userPassword, UserRole role){
        this.userName = userName;
        this.email = email;
        this.userPassword = userPassword;
        this.role = role;


    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setUserId(long userId) {
        this.userID = userId;
    }
}
