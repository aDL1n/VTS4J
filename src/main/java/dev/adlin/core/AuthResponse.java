package dev.adlin.core;

public class AuthResponse implements IResponse {
    @SerializedName("messageType")
    private String messageType;
    @SerializedName("requestID")
    private String requetsId;
    @SerializedName("data")
    private JsonObject data;

    private AuthData authData = new AuthData();

    public AuthResponse() {
        authData.setAuthToken(data.get("authenticationToken").getAsString());
    }  

    public class AuthData {
        private String authToken;

        public String getAuthToken() {
            return authToken
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }
    }

    public String getMessageType() {
        return messageType;
    }

    public String getRequetsId() {
        return requetsId;
    }

    public JsonObject getData() {
        return data;
    }
}