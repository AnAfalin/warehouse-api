package ru.lazarenko.warehouse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.lazarenko.warehouse.entity.Role;
import ru.lazarenko.warehouse.entity.User;
import ru.lazarenko.warehouse.model.UserRole;
import ru.lazarenko.warehouse.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "pass123";

    @PostConstruct
    private void generate() {
        createAdmin();
    }

    private void createAdmin() {

        Role roleAdmin = new Role();
        roleAdmin.setName(UserRole.ADMIN);

        Role roleUser = new Role();
        roleUser.setName(UserRole.MANAGER);

        if (userRepository.findByUsername(USERNAME).isPresent()) {
            return;
        }

        User user = User.builder()
                .username(USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .roles(List.of(roleAdmin, roleUser))
                .registrationDate(LocalDate.now())
                .build();

        userRepository.save(user);
    }

}
