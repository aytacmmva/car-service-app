package com.carservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import com.carservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
     EntityManagerFactory emf = Persistence.createEntityManagerFactory("carServicePU");

    public default Object save(Car car) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(car); // Bazaya yazma əmri
        em.getTransaction().commit();
        em.close();
        return null;
    }

    public default Optional<Car> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        Car car = em.find(Car.class, id);
        em.close();
        return Optional.ofNullable(car);
    }
}