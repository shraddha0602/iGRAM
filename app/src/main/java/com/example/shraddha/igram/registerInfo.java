package com.example.shraddha.igram;

public class registerInfo {
    public String name;
    public String room;
    public String hostel;
    public String roll;
    public String email;
    public String password;
    private String tokenId;

    public registerInfo() {
    }

    public registerInfo(String name, String room, String hostel, String roll, String email, String password, String tokenId) {
        this.name = name;
        this.room = room;
        this.hostel = hostel;
        this.roll = roll;
        this.email = email;
        this.password = password;
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoom() {
        return room;
    }

    public String getHostel() {
        return hostel;
    }

    public String getRoll() {
        return roll;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {

        this.tokenId = tokenId;
    }
}

