package com.lit_map_BackEnd.domain.author.service;


import com.lit_map_BackEnd.domain.author.entity.Author;

public interface AuthorService {
    Author insertAuthor(String author);

    Author checkAuthor(String name);
}
