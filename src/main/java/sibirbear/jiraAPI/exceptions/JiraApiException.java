package sibirbear.jiraAPI.exceptions;

public class JiraApiException extends Exception{

    public JiraApiException() {
        super();
    }

    public JiraApiException(String message) {
        super(message);
    }

    public JiraApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public JiraApiException(Throwable cause) {
        super(cause);
    }

}
