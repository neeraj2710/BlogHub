package com.mardox.bloghub.service;

import com.mardox.bloghub.dto.CategoryRequestDto;
import com.mardox.bloghub.dto.CategoryResponseDto;
import com.mardox.bloghub.dto.CategoryUpdateDto;
import com.mardox.bloghub.entity.Category;
import com.mardox.bloghub.exception.ResouceAlreadyExistsException;
import com.mardox.bloghub.exception.ResourceNotFoundException;
import com.mardox.bloghub.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private CategoryRepository repository;

    @Autowired
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public CategoryResponseDto createCategory(CategoryRequestDto createDto){
        if(repository.existsByCatName(createDto.getCatName()))
            throw new ResouceAlreadyExistsException("Category with name : "+createDto.getCatName()+" already exists.");
        Category category = new Category();
        category.setCatName(createDto.getCatName());
        category.setDescription(createDto.getDescr());
        Category saveCat = repository.save(category);
        return new CategoryResponseDto(saveCat.getId(), saveCat.getCatName(), saveCat.getDescription(), 0);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll(){
        return repository.findAll().stream().map(
                category -> new CategoryResponseDto(
                        category.getId(),
                        category.getCatName(),
                        category.getDescription(),
                        category.getPostList() != null ? category.getPostList().size() : 0
                )
        ).collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id){
        Category cat = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id : "+id+" does not exists")
        );
        return new CategoryResponseDto(cat.getId(), cat.getCatName(), cat.getDescription(), 0);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryUpdateDto updateDto){
        Category cat = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id : "+id+" does not exists")
        );
        if(updateDto == null || (updateDto.getCatName() == null && updateDto.getDescr() == null))
            throw new IllegalArgumentException("At least one field must be update");
        if(updateDto.getCatName() != null)
            cat.setCatName(updateDto.getCatName());
        if(updateDto.getDescr() != null)
            cat.setDescription(updateDto.getDescr());
        Category saveCat = repository.save(cat);
        return new CategoryResponseDto(saveCat.getId(), saveCat.getCatName(), saveCat.getDescription(), 0);
    }

    public void deleteCategory(Long id){
        Category cat = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id : "+id+" does not exists")
        );
        repository.delete(cat);
    }
}
