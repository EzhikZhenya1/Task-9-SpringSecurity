package ru.itmentor.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String findUsers(Model model) {
        List<User> users = userService.findUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/save")
    public String saveUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "/save";
    }

    @PostMapping
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                           @RequestParam("roles") Set<Long> roleId) {
        if (bindingResult.hasErrors()) {
            return "/save";
        }
        Set<Role> roles = roleId.stream()
                .map(id -> roleRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String updateUser(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id).orElseThrow());
        model.addAttribute("roles", roleRepository.findAll());
        return "/update";
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                             @RequestParam("roles") Set<Long> roleId) {
        if (bindingResult.hasErrors()) {
            return "/update";
        }
        Set<Role> roles = roleId.stream()
                .map(id -> roleRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
