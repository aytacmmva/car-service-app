package repository;

import model.Repairer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepairerRepository {
    private final Map<Long, Repairer> database = new HashMap<>();
    private List<Repairer> repairers = new ArrayList<>();

    public RepairerRepository() {
        // Mock data for testing
        database.put(1L, new Repairer(1L, "John Doe"));
        database.put(2L, new Repairer(2L, "Jane Smith"));
    }

    public List<Repairer> getAvailableRepairers() {
        return repairers.stream()
                .filter(Repairer::isAvailable)
                .collect(Collectors.toList());
    }

    public Repairer findById(Long id) {
        return database.get(id);
    }

    public void addRepairer(Repairer johnDoe) {
    }
}