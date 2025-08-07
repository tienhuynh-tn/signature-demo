package com.demo.repository;

import com.demo.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfigRepository extends JpaRepository<Config, UUID> {
    Optional<Config> findById(UUID id);
}
