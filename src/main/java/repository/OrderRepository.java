package repository;
import model.Order;
import java.io.ObjectInputFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRepository implements Serializable {
    private List<Order> orders;
    private final Map<Long, Order> database = new HashMap<>();
    private long currentId = 1;

    public Order save(Order order) {
        if (order.getId() == null) {

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

    public List<Order> findByStatus(ObjectInputFilter.Status status) {
        return orders.stream()
                .filter(o -> o.getStatus().name().equals(status.name()))
                .collect(Collectors.toList());
    }

    public double getTotalRevenue() {
        return orders.stream()
                .mapToDouble(Order::getPrice)
                .sum();
    }
}