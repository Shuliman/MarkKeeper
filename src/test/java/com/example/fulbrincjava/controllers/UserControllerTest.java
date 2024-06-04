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
import com.example.fulbrincjava.services.JwtService;
import com.example.fulbrincjava.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = UserController.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L); // Set appropriate type if different
        existingUser.setLogin("Test login");
        existingUser.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        given(userService.allUsers()).willReturn(Collections.singletonList(existingUser));
        given(userService.getCurrentUser()).willReturn(Optional.ofNullable(existingUser));
        given(userService.getUserById(Math.toIntExact(existingUser.getId()))).willReturn(existingUser);
        // Example of mocking JwtService behavior in your test setup
        given(jwtService.extractUsername(anyString())).willReturn(existingUser.getEmail());
    }

    @Test
    public void shouldGetAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/me").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$[0].email").value(existingUser.getEmail()));
    }

    @Test
    public void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", existingUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(existingUser.getLogin()))
                .andExpect(jsonPath("$.email").value(existingUser.getEmail()));
    }
}
