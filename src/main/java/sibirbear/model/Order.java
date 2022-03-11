package sibirbear.model;

/*
 * Модель для хранения данных, которые будут использованы для создания заявки в Jira
 */

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final String project;
    private final String reporter;
    private String issueType;
    private String nameOrder;
    private String description;
    private String contact;
    private String department;
    private final List<String> attachment;
    private boolean isCreated;

    public Order(String project, String reporter) {
        this.project = project;
        this.reporter = reporter;
        this.attachment = new ArrayList<>();
        this.isCreated = false;
    }

    public String getProject() {
        return project;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getReporter() {
        return reporter;
    }

    public String getNameOrder() {
        return nameOrder;
    }

    public void setNameOrder(String nameOrder) {
        this.nameOrder = nameOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
}
