package service;


import enums.OrderStatus;
import model.Order;
import model.Repairer;
import repository.OrderRepository;
import repository.RepairerRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RepairerRepository repairerRepository;

    public OrderService(OrderRepository orderRepository, RepairerRepository repairerRepository) {
        this.orderRepository = orderRepository;
        this.repairerRepository = repairerRepository;
    }

    public void openOrder(double price) {
        Order order = new Order(null, price);
        Order savedOrder = orderRepository.save(order);
        System.out.println("Success: Opened new order with ID " + savedOrder.getId());
    }

    public void assignRepairer(Long orderId, Long repairerId) {
        Order order = getOrderIfOpen(orderId);
        Repairer repairer = repairerRepository.findById(repairerId);
        
        if (repairer == null) {
            throw new IllegalArgumentException("model.Repairer not found!");
        }
        
        order.addRepairer(repairer);
        orderRepository.save(order);
        System.out.println("Success: Assigned repairer " + repairer.getName() + " to order " + orderId);
    }

    public void completeOrder(Long orderId) {
        Order order = getOrderIfOpen(orderId);
        
        if (order.getAssignedRepairers().isEmpty()) {
            throw new IllegalStateException("Cannot complete order without assigned repairers!");
        }
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionTimestamp(LocalDateTime.now());
        orderRepository.save(order);
        System.out.println("Success: model.Order " + orderId + " completed.");
    }

    public void cancelOrder(Long orderId) {
        Order order = getOrderIfOpen(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        System.out.println("Success: model.Order " + orderId + " canceled.");
    }

    public void listOrders(int page, int size, String sortBy) {
        List<Order> orders = orderRepository.findAll();

        // Sorting logic
        Comparator<Order> comparator;
        switch (sortBy.toLowerCase()) {
            case "price": comparator = Comparator.comparing(Order::getPrice); break;
            case "opening_timestamp": comparator = Comparator.comparing(Order::getOpeningTimestamp); break;
            case "completion_timestamp": comparator = Comparator.comparing(Order::getCompletionTimestamp, Comparator.nullsLast(Comparator.naturalOrder())); break;
            case "status": comparator = Comparator.comparing(Order::getStatus); break;
            case "id":
            default: comparator = Comparator.comparing(Order::getId); break;
        }

        orders.sort(comparator);

        // Pagination logic
        int fromIndex = (page - 1) * size;
        if (fromIndex >= orders.size()) {
            System.out.println("No orders on this page.");
            return;
        }
        int toIndex = Math.min(fromIndex + size, orders.size());
        
        List<Order> paginatedOrders = orders.subList(fromIndex, toIndex);
        paginatedOrders.forEach(System.out::println);
    }

    private Order getOrderIfOpen(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("model.Order not found!");
        }
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("model.Order is not currently open!");
        }
        return order;
    }
}