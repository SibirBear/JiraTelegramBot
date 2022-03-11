package sibirbear.store;

import sibirbear.model.Order;

import java.util.HashMap;
import java.util.Map;

public class StoreOrders implements IStore<Order>{

    private final Map<Long, Order> storeOrders = new HashMap<>();

    @Override
    public void save(long chatID, Order order) {
        storeOrders.put(chatID, order);
    }

    @Override
    public Order get(long chatId) {
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
}
