package sibirbear.service;

import sibirbear.config.Config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Класс для установки и закрытия HTTP-соединения
 */

public class ConnectHTTP {

    private String url;
    private TypeRequestHTTP typeRequestHTTP;
    private final String HTTP_PROPERTY_CONTENT = "Content-Type";
    private final String HTTP_PROPERTY_CONTENT_TYPE = "application/json";
    private final String HTTP_PROPERTY_AUTH = "Authorization";
    private final String HTTP_PROPERTY_AUTH_JIRA = Config.getAuthJira();

    public ConnectHTTP(final String url, final TypeRequestHTTP typeRequestHTTP) {
        this.url = url;
        this.typeRequestHTTP = typeRequestHTTP;
    }

    public HttpURLConnection connect() throws IOException {
        URL connectUrl = new URL(url);
        HttpURLConnection http = (HttpURLConnection)connectUrl.openConnection();
        http.setRequestMethod(typeRequestHTTP.getType());
        http.setRequestProperty(HTTP_PROPERTY_CONTENT, HTTP_PROPERTY_CONTENT_TYPE);
        http.setRequestProperty(HTTP_PROPERTY_AUTH, HTTP_PROPERTY_AUTH_JIRA);
        return http;

    }

    public void close(HttpURLConnection http) {
        http.disconnect();
    }

}
