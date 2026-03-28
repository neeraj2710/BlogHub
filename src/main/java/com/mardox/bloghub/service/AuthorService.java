package com.mardox.bloghub.service;

import com.mardox.bloghub.dto.AuthorResponseDto;
import com.mardox.bloghub.dto.AuthorUpdateDto;
import com.mardox.bloghub.entity.Author;
import com.mardox.bloghub.exception.ResourceNotFoundException;
import com.mardox.bloghub.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private AuthorRepository repository;

    @Autowired
    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public List<Author> getAllUsers(){
        return repository.findAll();
    }

    public Author getUserById(Long id){
        return repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Author with id : " + id +" found")
        );
    }

    public Author updateAuthor(Long id, AuthorUpdateDto dto){
        Author author = this.getUserById(id);

        if(dto.getName()==null && dto.getEmail()==null && dto.getAbout()==null)
            throw new RuntimeException("Empty objects not allowed");

        if(dto.getName() != null && dto.getName().isBlank())
            throw new RuntimeException("Name cannot be Blank");

        if(dto.getAbout() != null && dto.getAbout().isBlank())
            throw new RuntimeException("Name cannot be Blank");

        if(dto.getName()!=null)
            author.setName(dto.getName());
        if(dto.getEmail()!=null)
            author.setEmail(dto.getEmail());
        if(dto.getAbout()!=null)
            author.setAbout(dto.getAbout());

        return repository.save(author);
    }

    public void deleteUser(Long id){
        repository.delete(this.getUserById(id));
    }
}
