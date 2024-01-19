package com.example.fulbrincjava.services;

import com.example.fulbrincjava.dtos.PostDTO;
import com.example.fulbrincjava.entities.Post;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.exceptions.PostNotFoundException;
import com.example.fulbrincjava.exceptions.UserNotFoundException;
import com.example.fulbrincjava.repositories.PostRepository;
import com.example.fulbrincjava.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<PostDTO> getPostsByCurrentUser() {
        User currentUser = userService.getCurrentUser().orElseThrow(()
                -> new RuntimeException("No authenticated user found"));
        Optional<User> user = Optional.ofNullable(currentUser);
        return postRepository.findByUser(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private PostDTO convertToDto(Post post) {
        PostDTO postDto = new PostDTO();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setUserId(post.getUser().getId());
        return postDto;
    }
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    // Creates a new post
    public Post createPost(Post post) {
        // TODO: Add business logic here if any
        return postRepository.save(post);
    }

    public Post updatePost(Post postDetails) {
        Optional<Post> post = postRepository.findById(postDetails.getId());
        if(post.isEmpty()){
            throw new PostNotFoundException("Post not found");
        }
        Post existingPost = post.get();
        existingPost.setTitle(postDetails.getTitle());
        existingPost.setDescription(postDetails.getDescription());
        return postRepository.save(existingPost);
    }

    public void deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty()){
            throw new PostNotFoundException("Post not found");
        }
        postRepository.delete(post.get());
    }
}