package sibirbear.store;

import sibirbear.jiraAPI.issue.Issue;

import java.util.Map;

public interface IStore<T> {

    void save(long chatID, T model);

    T get(long chatId);

    boolean contains(long chatId);

    void delete(long chatId);

    Map <Long, T> getAll();

}
