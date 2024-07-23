package com.lit_map_BackEnd.domain.genre.service;

import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{

    private final GenreRepository genreRepository;

    @Override
    public Genre insertGenre(String genre) {
        return genreRepository.save(Genre.builder().name(genre).build());
    }

    @Override
    public Genre checkGenre(String name) {
        Genre genre = genreRepository.findByName(name);
        if (genre == null) {
            genre = insertGenre(name);
        }
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }
}
