package com.example.fulbrincjava.services;

import com.example.fulbrincjava.dtos.UserDto;
import com.example.fulbrincjava.entities.Role;
import com.example.fulbrincjava.entities.RoleEnum;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.exceptions.UserAlreadyExists;
import com.example.fulbrincjava.exceptions.RoleNotFoundException;
import com.example.fulbrincjava.repositories.RoleRepository;
import com.example.fulbrincjava.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, RoleRepository roleRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User signup(UserDto input) throws RoleNotFoundException {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new UserAlreadyExists("User with email " + input.getEmail() + " already exists.");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);

        if (optionalRole.isEmpty()) {
            throw new RoleNotFoundException("Role USER not found!");
        }

        var user = new User()
                .setLogin(input.getLogin())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole.get());

        return userRepository.save(user);
    }


    public User authenticate(UserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}