package tourist_cocoon.dto;

import java.time.OffsetDateTime;

public class GoogleOAuthStatusDTO {
    private boolean connected;
    private String calendarId;
    private OffsetDateTime connectedAt;

    public GoogleOAuthStatusDTO() {}

    public GoogleOAuthStatusDTO(boolean connected, String calendarId, OffsetDateTime connectedAt) {
        this.connected = connected;
        this.calendarId = calendarId;
        this.connectedAt = connectedAt;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public OffsetDateTime getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(OffsetDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }
}

