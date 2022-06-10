package info.fermercentr.store;

import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import info.fermercentr.model.Steps;
import info.fermercentr.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static info.fermercentr.core.CoreConstants.NOT_AUTH;

public class StoreUsers implements IStore<User> {

    private final Map<Long, User> storeUsers = new HashMap<>();

    @Override
    public void save(long chatID, User user) {
        storeUsers.put(chatID, user);
    }

    @Override
    public User get(long chatId) throws JiraApiException {
        if (!contains(chatId)) {
            save(chatId, new User(NOT_AUTH, Steps.STEP100));
            throw new JiraApiException("ERROR! There are no user: " + chatId + " in store!");
        }
        return storeUsers.get(chatId);
    }

    @Override
    public boolean contains(long chatId) {
        return storeUsers.containsKey(chatId);
    }

    @Override
    public void delete(long chatId) {
        storeUsers.remove(chatId);
    }

    @Override
    public Map<Long, User> getAll() {
        return storeUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoreUsers)) return false;
        StoreUsers that = (StoreUsers) o;
        return storeUsers.equals(that.storeUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeUsers);
    }

    @Override
    public String toString() {
        return "StoreUsers{" +
                "storeUsers=" + storeUsers + "\n" +
                '}';
    }
}
