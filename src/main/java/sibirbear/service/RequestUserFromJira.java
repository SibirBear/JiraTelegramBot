package sibirbear.service;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RequestUserFromJira {

    public static int findUserJira(String user) {
        int responseUser = 404;
        HttpURLConnection http;
        String url = "https://jira.fermer-centr.shop/rest/api/2/user?key=" + user;
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
