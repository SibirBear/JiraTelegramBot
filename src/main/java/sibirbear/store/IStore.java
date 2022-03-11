package sibirbear.store;

public interface IStore<T> {

    void save(long chatID, T model);

    T get(long chatId);

    boolean contains(long chatId);

    void delete(long chatId);

}
