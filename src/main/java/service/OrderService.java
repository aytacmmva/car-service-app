package service;

import enums.OrderStatus;
import model.Order;
import model.Repairer;
import repository.OrderRepository;
import repository.RepairerRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository orderRepository;
    private final RepairerRepository repairerRepository;

    public OrderService(OrderRepository orderRepository,
                        RepairerRepository repairerRepository) {

        this.orderRepository = orderRepository;
        this.repairerRepository = repairerRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getTopExpensiveOrders() {

        return orderRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparingDouble(Order::getPrice)
                                .reversed()
                )
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Order> searchByCarModel(String model) {

        return orderRepository.findAll()
                .stream()
                .filter(order ->
                        order.getCarModel()
                                .equalsIgnoreCase(model)
                )
                .collect(Collectors.toList());
    }

    public boolean hasOrderWithRepairer(String repairerName) {

        return orderRepository.findAll()
                .stream()
                .anyMatch(order ->
                        order.getRepairer()
                                .getName()
                                .equals(repairerName)
                );
    }

    public Order openOrder(double price) {

        Order order = new Order(null, price);

        Order savedOrder =
                orderRepository.save(order);

        System.out.println(
                "Success: Opened new order with ID "
                        + savedOrder.getId()
        );

        return savedOrder;
    }

    public void assignRepairer(Long orderId,
                               Long repairerId) {

        Order order =
                getOrderIfOpen(orderId);

        Repairer repairer =
                repairerRepository.findById(repairerId);

        if (repairer == null) {
            throw new IllegalArgumentException(
                    "Repairer not found!"
            );
        }

        order.addRepairer(repairer);

        orderRepository.save(order);

        System.out.println(
                "Success: Assigned repairer "
                        + repairer.getName()
                        + " to order "
                        + orderId
        );
    }

    public void completeOrder(Long orderId) {

        Order order =
                getOrderIfOpen(orderId);

        if (order.getAssignedRepairers().isEmpty()) {

            throw new IllegalStateException(
                    "Cannot complete order without assigned repairers!"
            );
        }

        order.setStatus(OrderStatus.COMPLETED);

        order.setCompletionTimestamp(
                LocalDateTime.now()
        );

        orderRepository.save(order);

        System.out.println(
                "Success: Order "
                        + orderId
                        + " completed."
        );
    }

    public void cancelOrder(Long orderId) {

        Order order =
                getOrderIfOpen(orderId);

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        System.out.println(
                "Success: Order "
                        + orderId
                        + " canceled."
        );
    }

    public void listOrders(int page,
                           int size,
                           String sortBy) {

        List<Order> orders =
                orderRepository.findAll();

        Comparator<Order> comparator;

        switch (sortBy.toLowerCase()) {

            case "price":
                comparator =
                        Comparator.comparing(
                                Order::getPrice
                        );
                break;

            case "opening_timestamp":
                comparator =
                        Comparator.comparing(
                                Order::getOpeningTimestamp
                        );
                break;

            case "completion_timestamp":
                comparator =
                        Comparator.comparing(
                                Order::getCompletionTimestamp,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()
                                )
                        );
                break;

            case "status":
                comparator =
                        Comparator.comparing(
                                Order::getStatus
                        );
                break;

            case "id":
            default:
                comparator =
                        Comparator.comparing(
                                Order::getId
                        );
                break;
        }

        orders.sort(comparator);

        int fromIndex =
                (page - 1) * size;

        if (fromIndex >= orders.size()) {

            System.out.println(
                    "No orders on this page."
            );

            return;
        }

        int toIndex =
                Math.min(fromIndex + size,
                        orders.size());

        List<Order> pagedOrders =
                orders.subList(fromIndex, toIndex);

        pagedOrders.forEach(System.out::println);
    }

    private Order getOrderIfOpen(Long orderId) {

        Order order =
                orderRepository.findById(orderId);

        if (order == null) {

            throw new IllegalArgumentException(
                    "Order not found!"
            );
        }

        if (order.getStatus()
                == OrderStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Order already completed!"
            );
        }

        if (order.getStatus()
                == OrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Order already canceled!"
            );
        }

        return order;
    }
}