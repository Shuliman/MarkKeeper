
package com.example.fulbrincjava.repositories;

import com.example.fulbrincjava.entities.Post;
import com.example.fulbrincjava.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByUser(Optional<User> user);
}
