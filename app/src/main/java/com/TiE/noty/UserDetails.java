package com.TiE.noty;

public class UserDetails {

    private String email,password,name;

    private String profile_photo;

    public UserDetails() {
    }

    public UserDetails(String email, String password, String name)  {
        this.email = email;
        this.password = password;
        this.name=name;

    }

    public UserDetails(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }
}
