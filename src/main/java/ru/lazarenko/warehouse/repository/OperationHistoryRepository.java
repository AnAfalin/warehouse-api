package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.OperationHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationHistoryRepository extends JpaRepository<OperationHistory, Integer> {

    List<OperationHistory> findAllByDateIsAfter(LocalDateTime time);
}
