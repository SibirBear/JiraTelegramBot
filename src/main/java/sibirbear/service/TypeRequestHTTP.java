package sibirbear.service;

public enum TypeRequestHTTP {
    GET("GET"),
    POST("POST");

    private final String type;

    TypeRequestHTTP(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
