package ru.itmentor.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserServiceInterface{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        Role admin = createRole("ADMIN");
        Role user = createRole("USER");

        createUser("admin", "admin", admin, user);
        createUser("user", "user", user);
    }

    public void createUser(String username, String password, Role... roles) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setUsername(username);
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>(Set.of(roles)));
        userRepository.save(user);
    }

    public Role createRole (String role) {
        return roleRepository.findByName(role)
                .orElseGet( () -> roleRepository.save(new Role(role)) );
    }


    @Override
    @Transactional(readOnly = true)
    public void saveUser(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User existingUser = userRepository.findById(user.getId()).orElseThrow();
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsers() {
        return userRepository.findAllByOrderByIdAsc();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
