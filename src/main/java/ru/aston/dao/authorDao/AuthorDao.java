package ru.aston.dao.authorDao;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

public interface AuthorDao {

    AuthorDto createAuthor(AuthorDtoShort authorDtoShort);

    List<AuthorDto> getAllAuthors();

    AuthorDto getAuthorById(int authorId);

    void deleteAuthor(int authorId);
}
