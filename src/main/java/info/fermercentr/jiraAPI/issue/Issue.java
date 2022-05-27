package info.fermercentr.jiraAPI.issue;

/*
 * Модель для хранения данных, которые будут использованы для создания заявки в Jira.
 * Параметры:
 *  project - id проекта;
 *  reporter - автор issue;
 *  creationTimeIssue - время создания;
 *  issueType - id issue;
 *  nameOrder - название issue;
 *  description - описание issue;
 *  contact - контактная информация для связи по issue;
 *  idanydesk - id для подключения Anydesk 9 знаков;
 *  department - подразделение, на которое распространяется issue;
 *  attachment - названия файлов в "обменнике" для прикрепления к issue;
 *  isCreated - признак, что issue по указанным параметрам создан в Jira;
 */

import info.fermercentr.jiraAPI.exceptions.JiraApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Issue {

    private final String project;
    private final String reporter;
    private final LocalDateTime creationTimeIssue;
    private String issueType;
    private String nameIssue;
    private String description;
    private String contact;
    private String idanydesk;
    private String department;
    private final List<String> attachment;
    private boolean isCreated;

    private final static int ID_ANYDESK_LENGTH = 9;

    public Issue(String project, String reporter) throws JiraApiException {
        if (isStringNullOrEmpty(project) || isStringNullOrEmpty(reporter)) {
            throw new JiraApiException("Issue cannot create with empty parameter or NPE." +
                    "\nParameters: project: "
                    + project + "; reporter: " + reporter + ";");
        }

        this.project = project;
        this.reporter = reporter;
        this.creationTimeIssue = LocalDateTime.now();
        this.attachment = new ArrayList<>();
        this.isCreated = false;
    }

    public String getProject() {
        return project;
    }

    public String getReporter() {
        return reporter;
    }

    public LocalDateTime getCreationTimeIssue() {
        return creationTimeIssue;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getNameIssue() {
        return nameIssue;
    }

    public void setNameIssue(String nameIssue) throws JiraApiException {
        if (isStringNullOrEmpty(nameIssue)
                || nameIssue.replaceAll("\\s+", "").isEmpty()) {
            throw new JiraApiException("Issue name cannot be Null or empty");
        }
        this.nameIssue = nameIssue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws JiraApiException {
        if (isStringNullOrEmpty(description)
                || description.replaceAll("\\s+", "").isEmpty()) {
            throw new JiraApiException("Issue description cannot be Null or empty");
        }
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) throws JiraApiException {
        if (isStringNullOrEmpty(contact)
                || contact.replaceAll("\\s+", "").isEmpty()) {
            throw new JiraApiException("Contact of Issue author cannot be Null or empty");
        }
        this.contact = contact;
    }

    public String getIdAnydesk() {
        return idanydesk;
    }

    public void setIdanydesk(String idanydesk) throws JiraApiException {
        if (idanydesk.trim().length() != ID_ANYDESK_LENGTH
                || !isDigit(idanydesk)) {
            throw new JiraApiException("ID Anydesk must be 9 numbers. String was: " + idanydesk);
        }
        this.idanydesk = idanydesk;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getAttachment() {
        return attachment;
    }

    public void addAttachmentFile(String attachmentFile) {
        this.attachment.add(attachmentFile);
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    private boolean isDigit(String str) throws NumberFormatException {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isStringNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return isCreated == issue.isCreated
                && project.equals(issue.project)
                && reporter.equals(issue.reporter)
                && creationTimeIssue.equals(issue.creationTimeIssue)
                && Objects.equals(issueType, issue.issueType)
                && Objects.equals(nameIssue, issue.nameIssue)
                && Objects.equals(description, issue.description)
                && Objects.equals(contact, issue.contact)
                && Objects.equals(idanydesk, issue.idanydesk)
                && Objects.equals(department, issue.department)
                && Objects.equals(attachment, issue.attachment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                project,
                reporter,
                creationTimeIssue,
                issueType,
                nameIssue,
                description,
                contact,
                idanydesk,
                department,
                attachment,
                isCreated);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "project='" + project + '\'' +
                ", reporter='" + reporter + '\'' +
                ", creationTimeIssue=" + creationTimeIssue +
                ", issueType='" + issueType + '\'' +
                ", nameIssue='" + nameIssue + '\'' +
                ", description='" + description + '\'' +
                ", contact='" + contact + '\'' +
                ", idanydesk='" + idanydesk + '\'' +
                ", department='" + department + '\'' +
                ", attachment=" + attachment +
                ", isCreated=" + isCreated +
                "}\n";
    }

}
