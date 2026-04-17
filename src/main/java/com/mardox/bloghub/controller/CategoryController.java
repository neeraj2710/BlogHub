package com.mardox.bloghub.controller;

import com.mardox.bloghub.dto.CategoryRequestDto;
import com.mardox.bloghub.dto.CategoryResponseDto;
import com.mardox.bloghub.dto.CategoryUpdateDto;
import com.mardox.bloghub.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto reqDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(reqDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok(service.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDto updateDto){
        return ResponseEntity.ok(service.updateCategory(id,updateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        service.deleteCategory(id);
        return  ResponseEntity.ok("Category deleted successfully.");
    }
}
