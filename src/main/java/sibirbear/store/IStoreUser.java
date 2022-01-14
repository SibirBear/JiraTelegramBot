package sibirbear.store;

import sibirbear.model.User;

public interface IStoreUser {

    void saveUser(final long chatID, User user);

    User getUser(long chatId);

}
