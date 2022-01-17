package sibirbear.store;

import sibirbear.model.Steps;
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

    @Override
    public boolean containsUser(long chatId) {
        return storeUser.containsKey(chatId);
    }

    @Override
    public void deleteUser(long chatId) {
        storeUser.remove(chatId);
    }
}
