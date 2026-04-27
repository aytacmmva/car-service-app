package model;


import enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private double price;
    private LocalDateTime openingTimestamp;
    private LocalDateTime completionTimestamp;
    private OrderStatus status;
    private List<Repairer> assignedRepairers;

    public Order(Long id, double price) {
        this.id = id;
        this.price = price;
        this.openingTimestamp = LocalDateTime.now();
        this.status = OrderStatus.OPEN;
        this.assignedRepairers = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public double getPrice() { return price; }
    public LocalDateTime getOpeningTimestamp() { return openingTimestamp; }
    public LocalDateTime getCompletionTimestamp() { return completionTimestamp; }
    public void setCompletionTimestamp(LocalDateTime completionTimestamp) { this.completionTimestamp = completionTimestamp; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<Repairer> getAssignedRepairers() { return assignedRepairers; }

    public void addRepairer(Repairer repairer) {
        this.assignedRepairers.add(repairer);
    }

    @Override
    public String toString() {
        return String.format("model.Order[ID: %d, Price: %.2f, Status: %s, Opened: %s, Completed: %s, Repairers: %s]",
                id, price, status, openingTimestamp, completionTimestamp != null ? completionTimestamp : "N/A", assignedRepairers);
    }
}