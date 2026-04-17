package com.mardox.bloghub.service;

import com.mardox.bloghub.dto.PostRequestDto;
import com.mardox.bloghub.dto.PostResponseDto;
import com.mardox.bloghub.dto.PostUpdateDto;
import com.mardox.bloghub.entity.Author;
import com.mardox.bloghub.entity.Category;
import com.mardox.bloghub.entity.Post;
import com.mardox.bloghub.exception.ResourceNotFoundException;
import com.mardox.bloghub.repository.AuthorRepository;
import com.mardox.bloghub.repository.CategoryRepository;
import com.mardox.bloghub.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;
    private AuthorRepository authorRepository;

    @Autowired
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, AuthorRepository authorRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    public Post createPost(PostRequestDto dto){
        if(dto.getAuthorId() == null)
            throw new RuntimeException("Author ID cannot be null");

        Author author = authorRepository.findById(dto.getAuthorId()).orElse(null);
        if(author == null)
            throw new ResourceNotFoundException("Author with id : "+dto.getAuthorId()+" does not exists.");
        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        if(category == null)
            throw new ResourceNotFoundException("Category with id : "+dto.getCategoryId()+" does not exists.");

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(author);
        post.setCategory(category);

        return postRepository.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Page<PostResponseDto> getAllPosts(int page,int size,String sortBy,String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Post> postPage = postRepository.findAll(pageable);
        List<PostResponseDto> dtoList = postPage.stream().map(
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
        return new PageImpl<>(dtoList,pageable,postPage.getTotalElements());
    }

    public Post getPostById(Long id){
        return postRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Post with id : "+id+" not found")
        );
    }

    public List<Post> searchPosts(String term){
        return postRepository.findByTitleContainingOrContentContaining(term.toLowerCase(),term.toLowerCase());
    }

    public List<Post> getPostByAuthor(Long id){
        Author author = authorRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Author with id : "+id+" not found")
        );
        return postRepository.findByAuthor(author);
    }

    public Post updatePost(Long postId, PostUpdateDto dto){
        Post post = getPostById(postId);
        if(dto == null || (dto.getTitle() == null && dto.getAuthorId() == null && dto.getCategoryId() == null && dto.getContent() == null))
            throw new RuntimeException("At least one field must be updated.");
        if(dto.getTitle() != null)
            post.setTitle(dto.getTitle());
        if(dto.getContent() != null)
            post.setContent(dto.getContent());
        if(dto.getAuthorId() != null){
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(
                    ()-> new ResourceNotFoundException("Author with id : "+dto.getAuthorId()+" not found")
            );
            post.setAuthor(author);
        }
        if(dto.getCategoryId() != null){
            Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                    ()->new ResourceNotFoundException("Category with id : "+dto.getCategoryId()+" does not exists.")
            );
            post.setCategory(category);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long id){
        Post post = getPostById(id);
        postRepository.delete(post);
    }

}
