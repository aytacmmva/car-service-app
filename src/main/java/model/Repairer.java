package model;

import java.io.Serializable;

public class Repairer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private boolean isAvailable;

    public Repairer(Long id, String name) {
        this.id = id;
        this.name = name;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return name + "(ID:" + id + ")";
    }

}