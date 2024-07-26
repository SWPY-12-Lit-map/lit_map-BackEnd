package com.lit_map_BackEnd.domain.genre.service;


import com.lit_map_BackEnd.domain.genre.entity.Genre;

import java.util.List;

public interface GenreService {
    Genre insertGenre(String genre);
    Genre checkGenre(String name);

    List<Genre> getGenres();

}
