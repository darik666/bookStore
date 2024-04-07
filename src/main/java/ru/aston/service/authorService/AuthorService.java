package ru.aston.service.authorService;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

public interface AuthorService {

    void deleteAuthor(int authorId);

    List<AuthorDto> getAllAuthors();

    AuthorDto createAuthor(AuthorDtoShort authorDtoShort);

    AuthorDto getAuthorById(int authorId);
}
