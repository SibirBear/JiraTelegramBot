package sibirbear.service;

import sibirbear.config.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RequestUserFromJira {

    public static int findUserJira(final String user) {
        int responseUser = 404;
        HttpURLConnection http;
        String url = Config.getUrlUser() + user;
        ConnectHTTP connectHTTP = new ConnectHTTP(url, TypeRequestHTTP.GET);
        try {
            http = connectHTTP.connect();
            responseUser = http.getResponseCode();
            connectHTTP.close(http);
        } catch (IOException e) {
            e.printStackTrace();            
        }

        return responseUser;
    }

}
