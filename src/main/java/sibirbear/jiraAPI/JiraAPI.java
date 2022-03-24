package sibirbear.jiraAPI;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JiraAPI {

    private final String HTTP_PROPERTY_CONTENT = "Content-Type";
    private final String HTTP_PROPERTY_CONTENT_TYPE = "application/json";
    private final String HTTP_PROPERTY_AUTH = "Authorization";
    private final String HTTP_PROPERTY_ATLAS_TOKEN = "X-Atlassian-Token";
    private final String HTTP_PROPERTY_NO_CHECK = "no-check";


    private final String JIRA_API_ISSUE = "/rest/api/2/issue/";
    private final String JIRA_API_LATEST_ISSUE = "/rest/api/latest/issue/";
    private final String JIRA_API_ATTACHMENTS = "/attachments";

    private final String BASE_URL;
    private final String CREDENTIALS;

    /**
     * Принимает путь до Api Jira Atlassian
     * @param url строка, содержит URL до Api Jira
     */
    public JiraAPI(final String url, final String credentials) {
        this.BASE_URL = url;
        this.CREDENTIALS = credentials;

    }

    //создание заявки
    public String httpCreate(final String query) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(BASE_URL + JIRA_API_ISSUE);
        httpPost.setHeader(HTTP_PROPERTY_AUTH, CREDENTIALS);

        StringEntity requestEntity = new StringEntity(query, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);

        InputStream inputStream = httpResponse.getEntity().getContent();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();
        int charIndex;

        while ((charIndex = bufferedReader.read()) != -1) {
            stringBuilder.append((char) charIndex);
        }

        JSONObject json = new JSONObject(stringBuilder);

        return json.getString("key");

    }

    //добавить файлы к заявке
    public void addAttachment(final String issueKey, final String file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(BASE_URL + JIRA_API_LATEST_ISSUE + issueKey + JIRA_API_ATTACHMENTS);

        Header header = new BasicHeader(HTTP_PROPERTY_ATLAS_TOKEN, HTTP_PROPERTY_NO_CHECK);
        httpPost.setHeader(header);
        httpPost.setHeader(HTTP_PROPERTY_AUTH, CREDENTIALS);

        String fileExtension = file.split("\\.")[1];

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        //TODO сделать константой название файла и добавить цифру в название, чтобы изменялось по кол-ву загруженных файлов
        HttpEntity requestEntity = multipartEntityBuilder.addBinaryBody(
                "file",
                new File(file),
                ContentType.APPLICATION_OCTET_STREAM,
                "Attach_from_bot." + FilenameUtils.getExtension(file))
                .build();

        httpPost.setEntity(requestEntity);

        httpClient.execute(httpPost);

    }


    //базовый метод отправки http запроса

    //создание заявки от другого пользователя

    //добавление файлов

    //проверить пользователя

    //получить список заявок

    //получить список открытых заявок


}
