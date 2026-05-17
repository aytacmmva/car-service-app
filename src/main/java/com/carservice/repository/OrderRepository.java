package com.carservice.repository;



import enums.OrderStatus;
import com.carservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.repairers WHERE o.id = :id")
    Optional<Order> findByIdWithRepairers(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.repairers",
            countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithRepairers(Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.repairers WHERE o.status = :status",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Page<Order> findAllByStatusWithRepairers(@Param("status") OrderStatus status, Pageable pageable);

    boolean existsByIdAndStatus(Long id, OrderStatus status);
}
