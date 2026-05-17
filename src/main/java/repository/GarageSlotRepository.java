package repository;

import model.GarageSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GarageSlotRepository extends JpaRepository<GarageSlot, Long> {

    boolean existsBySlotNumber(String slotNumber);

    Optional<GarageSlot> findBySlotNumber(String slotNumber);
}
