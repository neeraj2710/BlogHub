package com.mardox.bloghub.repository;

import com.mardox.bloghub.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {

    boolean existsByEmail(String email);
    Optional<Author> findByEmail(String email);

}
