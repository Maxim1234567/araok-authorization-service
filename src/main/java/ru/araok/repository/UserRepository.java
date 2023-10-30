package ru.araok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.araok.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNameIgnoreCase(String name);

    Optional<User> findByNameAndPassword(String name, String password);
}
