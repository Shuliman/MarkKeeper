package com.example.fulbrincjava.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import com.example.fulbrincjava.controllers.UserController;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.repositories.UserRepository;
import com.example.fulbrincjava.services.JwtService;
import com.example.fulbrincjava.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    private JwtService jwtService;

    private User existingUser;

    private String token;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setLogin("Test login");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("password");

        given(userRepository.findByEmail(existingUser.getEmail())).willReturn(Optional.of(existingUser));
        given(userRepository.findById(Math.toIntExact(existingUser.getId()))).willReturn(Optional.of(existingUser));
        given(userRepository.findAll()).willReturn(Collections.singletonList(existingUser));

        token = jwtService.generateToken(existingUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(authentication.getPrincipal()).willReturn(existingUser);

        given(userService.getCurrentUser()).willReturn(Optional.of(existingUser));
        given(userService.getUserById(Math.toIntExact(existingUser.getId()))).willReturn(existingUser);
    }

    @Test
    public void shouldGetAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$[0].email").value(existingUser.getEmail()));
    }

    @Test
    public void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", existingUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }
}
