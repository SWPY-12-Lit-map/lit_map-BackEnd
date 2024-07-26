package com.lit_map_BackEnd.domain.category.service;


import com.lit_map_BackEnd.domain.category.entity.Category;

import java.util.List;

public interface CategoryService {
    Category insertCategory(String name);

    Category checkCategory(String name);

    List<Category> getCategories();
}
