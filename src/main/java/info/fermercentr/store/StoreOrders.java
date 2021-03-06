package info.fermercentr.store;

import info.fermercentr.jiraAPI.issue.Issue;

import java.util.HashMap;
import java.util.Map;

public class StoreOrders implements IStore<Issue>{

    private final Map<Long, Issue> storeOrders = new HashMap<>();

    @Override
    public void save(long chatID, Issue issue) {
        storeOrders.put(chatID, issue);
    }

    @Override
    public Issue get(long chatId) {
        return storeOrders.get(chatId);
    }

    @Override
    public boolean contains(long chatId) {
        return storeOrders.containsKey(chatId);
    }

    @Override
    public void delete(long chatId) {
        storeOrders.remove(chatId);
    }

    @Override
    public Map<Long, Issue> getAll() {
        return storeOrders;
    }

    @Override
    public String toString() {
        return "StoreOrders{" +
                "storeOrders=" + storeOrders +
                '}';
    }
}
