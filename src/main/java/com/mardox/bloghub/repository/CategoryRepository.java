package com.mardox.bloghub.repository;

import com.mardox.bloghub.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByCatName(String catName);

}
