package com.demo.repository;

import com.demo.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Optional<Application> findById(UUID id);

    @Query("SELECT a FROM Application a JOIN FETCH a.user")
    List<Application> findAllWithUsers();

}
