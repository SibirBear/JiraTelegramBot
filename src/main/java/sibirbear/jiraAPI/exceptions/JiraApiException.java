package sibirbear.jiraAPI.exceptions;

import java.io.IOException;

public class JiraApiException extends RuntimeException {

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
