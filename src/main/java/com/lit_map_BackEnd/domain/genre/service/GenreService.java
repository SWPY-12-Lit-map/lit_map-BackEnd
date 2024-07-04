package com.lit_map_BackEnd.domain.genre.service;


import com.lit_map_BackEnd.domain.genre.entity.Genre;

public interface GenreService {
    Genre insertGenre(String genre);

    Genre checkGenre(String name);
}
