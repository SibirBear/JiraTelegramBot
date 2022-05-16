package sibirbear.service;

import sibirbear.jiraAPI.issue.Issue;
import sibirbear.store.StoreOrders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteTempFilesOrders {

    private final StoreOrders storeOrders;
    private final List<String> listFilesFromIssues;

    public DeleteTempFilesOrders(StoreOrders storeOrders) {
        this.storeOrders = storeOrders;
        this.listFilesFromIssues = new ArrayList<>();
    }

    public void deleteTempFilesOrders() {
        List<String> listFiles = new DeleteTempFiles().getListOfTempFiles();

        listFiles.removeAll(getListFilesFromAttachment());

        for (String del : listFiles) {
            new File(del).delete();
        }
    }

    public List<String> getSavedListFiles() {
        return listFilesFromIssues;
    }

    private List<String> getListFilesFromAttachment() {
        for (Map.Entry<Long, Issue> map : storeOrders.getAll().entrySet()) {
            listFilesFromIssues.addAll(map.getValue().getAttachment());
        }

        return listFilesFromIssues;
    }

}
