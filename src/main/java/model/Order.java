package model;


import enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private double price;
    private String carModel;
    private LocalDateTime openingTimestamp;
    private LocalDateTime completionTimestamp;
    private OrderStatus status;
    private List<Repairer> assignedRepairers;
    private Repairer repairer;

    public Order(Long id, double price) {
        this.id = id;
        this.price = price;
        this.carModel = carModel;
        this.openingTimestamp = LocalDateTime.now();
        this.status = OrderStatus.OPEN;
        this.assignedRepairers = new ArrayList<>();
        this.repairer = repairer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }
    public String getCarModel() {
        return carModel;
    }


    public LocalDateTime getOpeningTimestamp() {
        return openingTimestamp;
    }

    public LocalDateTime getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(LocalDateTime completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public Repairer getRepairer() {
        return repairer;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
    public void setRepairer(Repairer repairer) {
        this.repairer = repairer;
    }

    public List<Repairer> getAssignedRepairers() {
        return assignedRepairers;
    }

    public void addRepairer(Repairer repairer) {
        this.assignedRepairers.add(repairer);
    }

    @Override
    public String toString() {
        return String.format("model.Order[ID: %d, Price: %.2f, Status: %s, Opened: %s, Completed: %s, Repairers: %s]",
                id, price, status, openingTimestamp, completionTimestamp != null ? completionTimestamp : "N/A", assignedRepairers);
    }

}