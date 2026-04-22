package model;

public class Repairer {
    private Long id;
    private String name;

    public Repairer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    
    @Override
    public String toString() { return name + "(ID:" + id + ")"; }
}