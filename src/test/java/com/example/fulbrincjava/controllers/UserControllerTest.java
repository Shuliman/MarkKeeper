package com.example.fulbrincjava.controllers;

import com.example.fulbrincjava.configs.JwtAuthenticationFilter;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.repositories.UserRepository;
import com.example.fulbrincjava.services.JwtService;
import com.example.fulbrincjava.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Active profile "test" to use test resources (e.g., H2 database)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {
        JwtService.class,
        JwtAuthenticationFilter.class,
        UserController.class
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    private User existingUser;
    private String token;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setLogin("Test login");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("password");

        // Mock repository methods
        given(userRepository.findByEmail(existingUser.getEmail()))
                .willReturn(Optional.of(existingUser));
        given(userRepository.findById(Math.toIntExact(existingUser.getId())))
                .willReturn(Optional.of(existingUser));
        given(userRepository.findAll())
                .willReturn(Collections.singletonList(existingUser));

        // Generate JWT token
        token = jwtService.generateToken(existingUser);

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(authentication.getPrincipal()).willReturn(existingUser);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock service methods
        given(userService.getCurrentUser()).willReturn(Optional.of(existingUser));
        given(userService.getUserById(Math.toIntExact(existingUser.getId())))
                .willReturn(existingUser);
    }

    @Test
    @DisplayName("Get the current authenticated user: 200 and data")
    public void shouldGetAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }

    @Test
    @DisplayName("Get all users: 200 and list")
    public void shouldGetAllUsers() throws Exception {
        given(userService.allUsers()).willReturn(Collections.singletonList(existingUser));

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$[0].email").value(existingUser.getEmail()));
    }

    @Test
    @DisplayName("Get user by ID: 200 and data")
    public void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", existingUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }
}