package com.mardox.bloghub.service;

import com.mardox.bloghub.dto.AuthResponseDto;
import com.mardox.bloghub.dto.LoginRequestDto;
import com.mardox.bloghub.dto.RegisterRequestDto;
import com.mardox.bloghub.entity.Author;
import com.mardox.bloghub.exception.ResouceAlreadyExistsException;
import com.mardox.bloghub.exception.ResourceNotFoundException;
import com.mardox.bloghub.repository.AuthorRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private AuthorRepository repository;

    @Autowired
    public AuthService(AuthorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AuthResponseDto registerAuthor(RegisterRequestDto dto){
        if(repository.existsByEmail(dto.getEmail()))
            throw new ResouceAlreadyExistsException("User with email "+dto.getEmail()+" already exists.");
        Author author = new Author();
        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        author.setPassword(dto.getPassword());
        author.setAbout(dto.getAbout());

        author = repository.save(author);

        return  new AuthResponseDto(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getRole(),
                "Registration Successful"
        );

    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto dto, HttpSession session){

        Author author = repository.findByEmail(dto.getEmail()).orElseThrow(
                ()-> new ResourceNotFoundException("Invalid email or password")
        );

        if(!author.getPassword().equals(dto.getPassword()))
            throw new ResourceNotFoundException("Invalid email or password");

        session.setAttribute("userId",author.getId());
        session.setAttribute("userName",author.getName());
        session.setAttribute("userEmail",author.getEmail());
        session.setAttribute("userRole",author.getRole());

        return  new AuthResponseDto(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getRole(),
                "Login Successful"
        );

    }

    public void logout(HttpSession session){
        session.invalidate();
    }

    public AuthResponseDto getCurrentUser(HttpSession session){
        Long userId = (Long)session.getAttribute("userId");
        if(userId == null)
            throw new ResourceNotFoundException("No User logged in");


        String userName = (String)session.getAttribute("userName");
        String userEmail = (String)session.getAttribute("userEmail");
        String userRole = (String)session.getAttribute("userRole");
        return new AuthResponseDto(
                userId,
                userName,
                userEmail,
                userRole,
                "Current User Details"
        );
    }



}
