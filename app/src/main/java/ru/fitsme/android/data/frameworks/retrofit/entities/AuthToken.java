package ru.fitsme.android.data.frameworks.retrofit.entities;

public class AuthToken {
    private String token;

    public AuthToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
