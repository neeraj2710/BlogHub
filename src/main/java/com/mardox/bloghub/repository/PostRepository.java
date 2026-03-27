package com.mardox.bloghub.repository;

import com.mardox.bloghub.entity.Author;
import com.mardox.bloghub.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByAuthor(Author author);
    List<Post> findByTitleContainingOrContentContaining(String title, String content);

}
