package repository;



import model.Repairer;

import java.util.HashMap;
import java.util.Map;

public class RepairerRepository {
    private final Map<Long, Repairer> database = new HashMap<>();

    public RepairerRepository() {
        // Mock data for testing
        database.put(1L, new Repairer(1L, "John Doe"));
        database.put(2L, new Repairer(2L, "Jane Smith"));
    }

    public Repairer findById(Long id) {
        return database.get(id);
    }
}