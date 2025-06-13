package com.edu.bcu.repository;

import com.edu.bcu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
    List<Category> findByStatus(Integer status);
} 