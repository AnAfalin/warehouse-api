package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManufactureAnalysisRepository extends JpaRepository<ManufactureAnalysis, Integer> {

    @Query(value = "select ma from ManufactureAnalysis ma where ma.reportDate > :fromDate and ma.reportDate < :toDate")
    List<ManufactureAnalysis> findAllByReportDateInPeriod(LocalDate fromDate, LocalDate toDate);
}
