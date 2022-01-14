package sibirbear.store;

import sibirbear.model.User;

import java.util.HashMap;
import java.util.Map;

public class StoreUser implements IStoreUser{

    private Map<Long, User> storeUser = new HashMap<>();

    @Override
    public void saveUser(long chatID, User user) {
        storeUser.put(chatID, user);
    }

    @Override
    public User getUser(long chatId) {
        return storeUser.get(chatId);
    }
}
