package com.mipt.sharipovrazil.dto.auth;

public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMinutes;

    public LoginResponse() {}

    public LoginResponse(String accessToken, long expiresInMinutes) {
        this.accessToken = accessToken;
        this.expiresInMinutes = expiresInMinutes;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresInMinutes() { return expiresInMinutes; }
    public void setExpiresInMinutes(long expiresInMinutes) { this.expiresInMinutes = expiresInMinutes; }
}