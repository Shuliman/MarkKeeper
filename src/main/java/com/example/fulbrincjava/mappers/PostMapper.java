package com.example.fulbrincjava.mappers;

import com.example.fulbrincjava.dtos.PostDTO;
import com.example.fulbrincjava.entities.Post;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.exceptions.UserNotFoundException;
import com.example.fulbrincjava.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserRepository userRepository;

    public PostMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Post convertToEntity(PostDTO postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        User user = userRepository.findById(Math.toIntExact(postDto.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + postDto.getUserId()));
        post.setUser(user);
        return post;
    }

    public PostDTO convertToDto(Post post) {
        PostDTO postDto = new PostDTO();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setUserId(post.getUser().getId());
        return postDto;
    }

}
