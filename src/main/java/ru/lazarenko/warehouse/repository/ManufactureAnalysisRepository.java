package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;

import java.time.LocalDateTime;

@Repository
public interface ManufactureAnalysisRepository extends JpaRepository<ManufactureAnalysis, Integer> {
}
