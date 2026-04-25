package tourist_cocoon.dto;

public class GoogleOAuthStartResponseDTO {
    private String authUrl;

    public GoogleOAuthStartResponseDTO() {}

    public GoogleOAuthStartResponseDTO(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
}

