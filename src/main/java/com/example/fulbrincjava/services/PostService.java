package com.example.fulbrincjava.services;

import com.example.fulbrincjava.dtos.PostDTO;
import com.example.fulbrincjava.entities.Post;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.exceptions.PostNotFoundException;
import com.example.fulbrincjava.exceptions.UserNotFoundException;
import com.example.fulbrincjava.repositories.PostRepository;
import com.example.fulbrincjava.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }
    //TODO move to self service
    private Post convertToEntity(PostDTO postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        User user = userRepository.findById(Math.toIntExact(postDto.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + postDto.getUserId()));
        post.setUser(user);
        return post;
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
    //TODO move to self service
    private PostDTO convertToDto(Post post) {
        PostDTO postDto = new PostDTO();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setUserId(post.getUser().getId());
        return postDto;
    }
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id " + id));
        return convertToDto(post);
    }

    public PostDTO createPost(PostDTO postDto) {
        User user = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        postDto.setUserId(user.getId());
        Post post = convertToEntity(postDto);
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }



    public PostDTO updatePost(Long id, PostDTO postDto) {
        Post postUpdate = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));

        postUpdate.setTitle(postDto.getTitle());
        postUpdate.setDescription(postDto.getDescription());

        Post updatedPost = postRepository.save(postUpdate);

        return convertToDto(updatedPost);
    }

    public void deletePost(Long id) {
        if(!postRepository.existsById(id)){
            throw new PostNotFoundException("Post not found");
        }
        postRepository.deleteById(id);
    }
}