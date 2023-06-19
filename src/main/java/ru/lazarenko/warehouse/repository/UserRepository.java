package ru.lazarenko.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.lazarenko.warehouse.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select u from User u left join fetch u.roles where u.username=:username")
    Optional<User> findByUsername(String username);

    @Query(value = "select u from User u left join fetch u.roles")
    List<User> findAllWithRoles();
}
