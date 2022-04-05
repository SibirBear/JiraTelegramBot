package sibirbear.service;

import sibirbear.config.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RequestUserFromJira {

    //FOR DELETE

//    public static int findUserJira(final String user) {
//        int responseUser = 404;
//        HttpURLConnection http;
//        String url = "https://jira.fermer-centr.shop/rest/api/2/user?key=" + user;
//        System.out.println(url);
//        ConnectHTTP connectHTTP = new ConnectHTTP(url, TypeRequestHTTP.GET);
//        try {
//            http = connectHTTP.connect();
//            responseUser = http.getResponseCode();
//            connectHTTP.close(http);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(responseUser);
//        return responseUser;
//    }

}
