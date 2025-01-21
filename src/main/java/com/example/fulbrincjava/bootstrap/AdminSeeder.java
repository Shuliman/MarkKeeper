package com.example.fulbrincjava.bootstrap;

import com.example.fulbrincjava.entities.Role;
import com.example.fulbrincjava.entities.RoleEnum;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.repositories.RoleRepository;
import com.example.fulbrincjava.repositories.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.login:}")
    private String adminLogin;

    @Value("${admin.default.email:}")
    private String adminEmail;

    @Value("${admin.default.password:}")
    private String adminPassword;

    public AdminSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        createAdministrator();
    }

    private void createAdministrator() {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(adminEmail);

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User()
                .setLogin(adminLogin)
                .setEmail(adminEmail)
                .setPassword(passwordEncoder.encode(adminPassword))
                .setRole(optionalRole.get());

        userRepository.save(user);
    }
}
