package sibirbear.store;

import sibirbear.model.Order;
import sibirbear.model.User;

import java.util.HashMap;
import java.util.Map;

public class StoreUsers implements IStore<User> {

    private final Map<Long, User> storeUsers = new HashMap<>();

    @Override
    public void save(long chatID, User user) {
        storeUsers.put(chatID, user);
    }

    @Override
    public User get(long chatId) {
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
}
