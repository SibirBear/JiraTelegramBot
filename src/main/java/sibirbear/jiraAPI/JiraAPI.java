package sibirbear.jiraAPI;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import sibirbear.jiraAPI.exceptions.JiraIssueURL;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JiraAPI {

    private final String HTTP_PROPERTY_CONTENT = "Content-Type";
    private final String HTTP_HEADER_CONTENT_TYPE_JSON = "application/json";
    private final String HTTP_PROPERTY_AUTH = "Authorization";
    private final String HTTP_PROPERTY_ATLAS_TOKEN = "X-Atlassian-Token";
    private final String HTTP_PROPERTY_NO_CHECK = "no-check";


    private final String JIRA_API_ISSUE = "/rest/api/2/issue/";
    private final String JIRA_API_SEARCH = "/rest/api/2/search";
    private final String JIRA_API_LATEST_ISSUE = "/rest/api/latest/issue/";
    private final String JIRA_API_ATTACHMENTS = "/attachments";

    private final String JIRA_JSON_KEY_ISSUES = "issues";
    private final String JIRA_JSON_KEY_FIELDS = "fields";
    private final String JIRA_JSON_KEY_DESCRIPTION = "description";

    private final String BASE_URL;
    private final String BASE_URI_SCHEME;
    private final String BASE_URI_HOST;
    private final String CREDENTIALS;

    /**
     * Принимает путь до Api Jira Atlassian
     * @param url строка, содержит URL до Api Jira
     * @param credentials строка, содержит логин и пароль для авторизации в Jira в кодировке base64
     */
    public JiraAPI(final String url, final String credentials) {
        this.BASE_URL = url;
        this.CREDENTIALS = credentials;

        String[] str = url.split("://");
        BASE_URI_SCHEME = str[0];
        BASE_URI_HOST = str[1];

    }

    //создание заявки
    public String httpCreateIssue(final String query) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(BASE_URL + JIRA_API_ISSUE);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);

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
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);

        //String fileExtension = file.split("\\.")[1];

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

    //получить список открытых заявок
    public List<JiraIssueURL> listIssues(final String reporter) throws URISyntaxException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(BASE_URI_SCHEME).setHost(BASE_URI_HOST).setPath(JIRA_API_SEARCH)
                .setParameter("jql", "reporter=" + reporter + " AND status NOT IN (5,6)");
        URI uri = uriBuilder.build();

        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, HTTP_HEADER_CONTENT_TYPE_JSON);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        BufferedReader br = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        boolean isEnd = false;

        while (!isEnd) {
            String line = br.readLine();
            if (line == null) {
                isEnd = true;
            } else {
                stringBuilder. append(line);
            }
        }

        JSONObject jsonMain = new JSONObject(stringBuilder.toString());
        JSONArray jsonArrayIssues = jsonMain.getJSONArray(JIRA_JSON_KEY_ISSUES);

        List<JiraIssueURL> listIssues = new ArrayList<>();

        for (int i = 0; i < jsonArrayIssues.length(); i++) {
            String key = jsonArrayIssues.getJSONObject(i).get("key").toString();
            String description = jsonArrayIssues.getJSONObject(i)
                    .getJSONObject(JIRA_JSON_KEY_FIELDS)
                    .get(JIRA_JSON_KEY_DESCRIPTION).toString();
            listIssues.add(new JiraIssueURL(key, description));
        }

        return listIssues;

    }

    //проверить пользователя


    //базовый метод отправки http запроса







}
