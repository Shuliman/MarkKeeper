package com.example.fulbrincjava.controllers;

import com.example.fulbrincjava.dtos.PostDTO;
import com.example.fulbrincjava.entities.Post;
import com.example.fulbrincjava.mappers.PostMapper;
import com.example.fulbrincjava.services.JwtService;
import com.example.fulbrincjava.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtService jwtService;


    private Post existingPost;
    private PostDTO existingPostDTO;

    @BeforeEach
    void setUp() {
        existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Test Title");
        existingPost.setDescription("Test Description");

        existingPostDTO = new PostDTO();
        existingPostDTO.setId(1L);
        existingPostDTO.setTitle("Test Title");
        existingPostDTO.setDescription("Test Description");

        // Setting up service mocking for createPost and getPostById
        given(postService.createPost(any(PostDTO.class))).willReturn(existingPostDTO);
        given(postService.getPostById(existingPost.getId())).willReturn(existingPostDTO);
    }


    @Test
    public void shouldCreatePost() throws Exception {
        // Data preparing
        String postJson = "{\"title\":\"Test Title\",\"description\":\"Test Description\"}";

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }


    @Test
    public void shouldGetExistingPost() throws Exception {
        mockMvc.perform(get("/posts/{id}", existingPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(existingPost.getTitle()));
    }
}
