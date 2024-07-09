package com.lit_map_BackEnd.domain.category.service;


import com.lit_map_BackEnd.domain.category.entity.Category;

public interface CategoryService {
    Category insertCategory(String name);

    Category checkCategory(String name);
}
