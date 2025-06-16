package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface extends UserDetailsService {
    void saveUser(User user);
    void deleteUser(User user);
    Optional<User> findUserById(long id);
    List<User> findUsers();
}
