package com.mardox.bloghub.controller;

import com.mardox.bloghub.dto.PostRequestDto;
import com.mardox.bloghub.dto.PostResponseDto;
import com.mardox.bloghub.dto.PostUpdateDto;
import com.mardox.bloghub.entity.Post;
import com.mardox.bloghub.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private PostService service;

    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody @Valid PostRequestDto dto, HttpSession session){
        Long id = (Long)session.getAttribute("userId");
        dto.setAuthorId(id);
        Post post = service.createPost(dto);
        PostResponseDto respDto = new PostResponseDto();
        respDto.setId(post.getId());
        respDto.setTitle(post.getTitle());
        respDto.setContent(post.getContent());
        respDto.setCategoryName(post.getCategory().getCatName());
        respDto.setCategoryId(post.getCategory().getId());
        respDto.setAuthorName(post.getAuthor().getName());
        respDto.setAuthorId(post.getAuthor().getId());
        respDto.setCreateDateTime(post.getCreatedAt());
        return new ResponseEntity<>(respDto, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(@RequestParam(required = false) String term){
        List<Post> postList = new ArrayList<>();
        if(term != null && !term.isBlank())
            postList = service.searchPosts(term);
        else
            postList = service.getAllPosts();
        List<PostResponseDto> dtoList = postList.stream().map(
                post -> {
                    PostResponseDto dto = new PostResponseDto();
                    dto.setId(post.getId());
                    dto.setTitle(post.getTitle());
                    dto.setContent(post.getContent());
                    dto.setCategoryName(post.getCategory().getCatName());
                    dto.setCategoryId(post.getCategory().getId());
                    dto.setAuthorName(post.getAuthor().getName());
                    dto.setAuthorId(post.getAuthor().getId());
                    dto.setCreateDateTime(post.getCreatedAt());
                    return dto;
                }
        ).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping()
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "3")int size,
            @RequestParam(defaultValue = "createdAt")String sortBy,
            @RequestParam(defaultValue = "DESC")String sortDir
    ){
        return ResponseEntity.ok(service.getAllPosts(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id){
        Post post = service.getPostById(id);
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCategoryName(post.getCategory().getCatName());
        dto.setCategoryId(post.getCategory().getId());
        dto.setAuthorName(post.getAuthor().getName());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setCreateDateTime(post.getCreatedAt());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my-post")
    public ResponseEntity<List<PostResponseDto>> getMyPost(@RequestAttribute("currentUserId")Long id){
        List<PostResponseDto> dtoList = service.getPostByAuthor(id).stream().map(
                post -> {
                    PostResponseDto dto = new PostResponseDto();
                    dto.setId(post.getId());
                    dto.setTitle(post.getTitle());
                    dto.setContent(post.getContent());
                    dto.setCategoryName(post.getCategory().getCatName());
                    dto.setCategoryId(post.getCategory().getId());
                    dto.setAuthorName(post.getAuthor().getName());
                    dto.setAuthorId(post.getAuthor().getId());
                    dto.setCreateDateTime(post.getCreatedAt());
                    return dto;
                }
        ).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateDto dto,
            @RequestAttribute("currentUserId")Long userId,
            @RequestAttribute("currentUserRole")String role
    ){
        Post post = service.getPostById(id);
        if(!post.getAuthor().getId().equals(userId) && !role.equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    "{\"error\":\"You can only update your own posts\"}"
            );
        Post updatedPost = service.updatePost(id,dto);
        PostResponseDto updatedDto = new PostResponseDto();
        updatedDto.setId(updatedPost.getId());
        updatedDto.setTitle(updatedPost.getTitle());
        updatedDto.setContent(updatedPost.getContent());
        updatedDto.setCategoryName(updatedPost.getCategory().getCatName());
        updatedDto.setCategoryId(updatedPost.getCategory().getId());
        updatedDto.setAuthorName(updatedPost.getAuthor().getName());
        updatedDto.setAuthorId(updatedPost.getAuthor().getId());
        updatedDto.setCreateDateTime(updatedPost.getCreatedAt());
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @RequestAttribute("currentUserId") Long userId,
            @RequestAttribute("currentUserRole") String role
    ){
        Post post = service.getPostById(id);
        if(!post.getAuthor().getId().equals(userId) && !role.equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    "{\"error\":\"You can only delete your own posts\"}"
            );
        service.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully.");
    }

}
