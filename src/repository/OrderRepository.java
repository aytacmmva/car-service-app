package repository;



import model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private final Map<Long, Order> database = new HashMap<>();
    private long currentId = 1;

    public Order save(Order order) {
        if (order.getId() == null) {
            // Simulate auto-increment
            Order newOrder = new Order(currentId++, order.getPrice());
            database.put(newOrder.getId(), newOrder);
            return newOrder;
        }
        database.put(order.getId(), order);
        return order;
    }

    public Order findById(Long id) {
        return database.get(id);
    }

    public List<Order> findAll() {
        return new ArrayList<>(database.values());
    }
}