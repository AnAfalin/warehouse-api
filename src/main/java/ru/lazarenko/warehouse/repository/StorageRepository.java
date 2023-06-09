package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.Storage;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {

    Optional<Storage> findByName(String name);
}
