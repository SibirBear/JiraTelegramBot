package info.fermercentr.service;

import info.fermercentr.jiraAPI.issue.Issue;
import info.fermercentr.store.StoreOrders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckOrdersTimeExpired {

    private final StoreOrders storeOrders;

    public CheckOrdersTimeExpired(StoreOrders storeOrders) {
        this.storeOrders = storeOrders;
    }

    public void execute() {
        List<Long> ext = new ArrayList<>();
        storeOrders.getAll().forEach((k, v) -> {
            if (isIssueExpired(storeOrders.get(k))) {
                ext.add(k);
            }
        });

        for(Long key : ext) {
            storeOrders.delete(key);
        }

    }

    private boolean isIssueExpired(Issue issue) {
        return issue.getCreationTimeIssue().isBefore(LocalDateTime.now().minusHours(12));
    }

}
