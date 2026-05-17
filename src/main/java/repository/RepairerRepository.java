package repository;

import model.Repairer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RepairerRepository extends JpaRepository<Repairer, Long> {

    List<Repairer> findAllByIdIn(Set<Long> ids);
}