package com.mardox.bloghub.controller;

import com.mardox.bloghub.dto.AuthorResponseDto;
import com.mardox.bloghub.dto.AuthorUpdateDto;
import com.mardox.bloghub.entity.Author;
import com.mardox.bloghub.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AuthorController {

    private AuthorService service;

    @Autowired
    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getUserById(@PathVariable Long id){

        Author author = service.getUserById(id);

        AuthorResponseDto dto = new AuthorResponseDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setEmail(author.getEmail());
        dto.setRole(author.getRole());
        dto.setAbout(author.getAbout());

        return ResponseEntity.ok(dto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AuthorUpdateDto dto, @RequestAttribute("userId") Long userId, @RequestAttribute("userRole") String userRole){

        if(!id.equals(userId) && !userRole.equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"Error\" : \"You can update only your profile\"}");

        Author author = service.updateAuthor(id,dto);

        AuthorResponseDto respDto = new AuthorResponseDto();
        respDto.setId(author.getId());
        respDto.setName(author.getName());
        respDto.setEmail(author.getName());
        respDto.setRole(author.getRole());
        respDto.setAbout(author.getAbout());

        return ResponseEntity.ok(respDto);
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> getAllUsers(){

        List<AuthorResponseDto> dtoList = service.getAllUsers().stream().map(
                author -> new AuthorResponseDto(
                        author.getId(),
                        author.getName(),
                        author.getEmail(),
                        author.getRole(),
                        author.getAbout()
                )
        ).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,@RequestAttribute("userId") Long userId,@RequestAttribute("userRole")String userRole){

        if(!id.equals(userId) && !userRole.equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"Error\" : \"You can delete only your profile\"}");

        service.deleteUser(id);
        return ResponseEntity.ok("Record deleted successfully");

    }
}
