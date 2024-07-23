package com.lit_map_BackEnd.domain.author.service;

import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.author.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService{

    private final AuthorRepository authorRepository;

    @Override
    public Author insertAuthor(String name) {
        return authorRepository.save(Author.builder().name(name).build());
    }

    @Override
    public Author checkAuthor(String name) {
        Author author = authorRepository.findByName(name);
        if (author == null) {
            author = insertAuthor(name);
        }
        return author;
    }
}
