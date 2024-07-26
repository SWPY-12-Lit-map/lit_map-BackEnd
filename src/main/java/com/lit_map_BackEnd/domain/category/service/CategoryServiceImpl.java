package com.lit_map_BackEnd.domain.category.service;

import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public Category insertCategory(String name) {
        return categoryRepository.save(Category.builder().name(name).build());
    }

    @Override
    public Category checkCategory(String name) {
        Category category = categoryRepository.findByName(name);
        if (category == null) {
            category = insertCategory(name);
        }
        return category;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }


}
