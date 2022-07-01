package info.fermercentr.jiraAPI;

import info.fermercentr.jiraAPI.schedule.JiraScheduleTask;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import info.fermercentr.jiraAPI.exceptions.JiraApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static info.fermercentr.jiraAPI.JiraConstants.JIRA_NAME_ATTACH_FILE;

public class JiraAPI {

    private static final Logger LOG = LogManager.getLogger(JiraAPI.class);

    private final String HTTP_HEADER_CONTENT_TYPE_JSON = "application/json";
    private final String HTTP_PROPERTY_ATLAS_TOKEN = "X-Atlassian-Token";
    private final String HTTP_PROPERTY_NO_CHECK = "no-check";


    private final String JIRA_API_ISSUE = "/rest/api/2/issue/";
    private final String JIRA_API_SEARCH = "/rest/api/2/search";
    private final String JIRA_API_LATEST_ISSUE = "/rest/api/latest/issue/";
    private final String JIRA_API_ATTACHMENTS = "/attachments";
    private final String JIRA_API_USER = "/rest/api/2/user";

    private final String JIRA_JSON_KEY_ISSUES = "issues";
    private final String JIRA_JSON_KEY_FIELDS = "fields";
    private final String JIRA_JSON_KEY_DESCRIPTION = "description";

    private final String BASE_URL;
    private final String BASE_URI_SCHEME;
    private final String BASE_URI_HOST;
    private final String CREDENTIALS;

    private int countFiles = 1;

    /**
     * Принимает путь до Api Jira Atlassian
     * @param url строка, содержит URL до Api Jira
     * @param credentials строка, содержит логин и пароль для авторизации в Jira в кодировке base64
     */
    public JiraAPI(final String url, final String credentials) throws JiraApiException {
        if (url == null || credentials == null
                || url.equals("") || credentials.equals("")) {
            throw new JiraApiException("URL to Jira or Credentials cannot be empty.");
        }

        this.BASE_URL = url;
        this.CREDENTIALS = credentials;

        String[] str = url.split("://");
        BASE_URI_SCHEME = str[0];
        BASE_URI_HOST = str[1];

    }

    //создание заявки
    //creating issue
    public String httpCreateIssue(final String query) throws JiraApiException {

        HttpPost httpPost = new HttpPost(BASE_URL + JIRA_API_ISSUE);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);

        StringEntity requestEntity = new StringEntity(query, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        StringBuilder stringBuilder = new StringBuilder();

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
             BufferedReader bufferedReader = new BufferedReader(
                 new InputStreamReader(
                         httpResponse.getEntity().getContent(),
                         StandardCharsets.UTF_8)
             )
        ) {
            int charIndex;
            while ((charIndex = bufferedReader.read()) != -1) {
                stringBuilder.append((char) charIndex);
            }

        } catch (IOException e) {
        throw new JiraApiException("Error with create HttpClient for creating issue. " + e);
        }

        JSONObject json = new JSONObject(stringBuilder.toString());

        return json.getString("key");

    }

    //добавить файл к заявке
    //adding attachment to issue
    public boolean addAttachment(final String issueKey, final String file) throws JiraApiException {

        boolean result = false;
        File fileToAttach = new File(file);

        HttpPost httpPost = new HttpPost(BASE_URL + JIRA_API_LATEST_ISSUE + issueKey + JIRA_API_ATTACHMENTS);

        Header header = new BasicHeader(HTTP_PROPERTY_ATLAS_TOKEN, HTTP_PROPERTY_NO_CHECK);
        httpPost.setHeader(header);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        HttpEntity requestEntity = multipartEntityBuilder.addBinaryBody(
                "file",
                fileToAttach,
                ContentType.APPLICATION_OCTET_STREAM,
                        JIRA_NAME_ATTACH_FILE + countFiles++ + "." + FilenameUtils.getExtension(file))
                .build();

        httpPost.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            httpClient.execute(httpPost);
            LOG.info("[" + getClass() + "] " + " File: " + fileToAttach + " uploaded successful!");
            result = true;
            boolean deleteResult = fileToAttach.delete();
            LOG.info("[" + getClass() + "] " + " File: " + fileToAttach + " deleted successful! " + deleteResult);

        } catch (IOException e) {
            LOG.error("[" + getClass() + "] " + " ERROR! " + e.getMessage());
            throw new JiraApiException("File " + file + " cannot add to " + issueKey + "\n" + e);
        }

        return result;

    }

    //добавить несколько файлов к заявке
    //adding multiple files to issue
    public boolean addMultiAttachment(final String issueKey, final List<String> files) {
        int count = 0;
        for (String file : files) {
            try {
                if (addAttachment(issueKey, file)) {
                    count++;
                }
            } catch (JiraApiException e) {
                e.printStackTrace();
            }
        }
        countFiles = 1;
        return count >= files.size();
    }

    //получить список открытых заявок
    // getting list of opened issues
    public List<JiraIssueURL> listIssues(final String reporter) throws JiraApiException {

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(BASE_URI_SCHEME).setHost(BASE_URI_HOST).setPath(JIRA_API_SEARCH)
                .setParameter("jql", "reporter=" + reporter + " AND status NOT IN (5,6)");
        URI uri = null;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new JiraApiException("Syntax error with url building parameters. " + e);
        }

        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, HTTP_HEADER_CONTENT_TYPE_JSON);

        StringBuilder stringBuilder = new StringBuilder();

        try(CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet)) {

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            boolean isEnd = false;
            while (!isEnd) {
                String line = br.readLine();
                if (line == null) {
                    isEnd = true;
                } else {
                    stringBuilder.append(line);
                }
            }
        } catch (IOException e) {
            throw new JiraApiException("Error with connecting to Jira or getting response on it. " + e);
        }

        JSONObject jsonMain = new JSONObject(stringBuilder.toString());
        JSONArray jsonArrayIssues = jsonMain.getJSONArray(JIRA_JSON_KEY_ISSUES);

        List<JiraIssueURL> listIssues = Collections.synchronizedList(new ArrayList<>());

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
    //check user in jira
    public int findUserJira(final String user) throws JiraApiException {
        int responseUser = 404;

        HttpGet httpGet = new HttpGet(BASE_URL + JIRA_API_USER + "?key=" + user);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, CREDENTIALS);

        LOG.info("[" + getClass() + "] " + " Trying to connect to Jira API...");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            LOG.info("[" + getClass() + "] " + " Connect to Jira API successful!");
            responseUser = response.getStatusLine().getStatusCode();

        } catch (Exception e) {
            throw new JiraApiException("ERROR! FindUserJira is corrupt. " + e);
        }

        return responseUser;

    }

}
