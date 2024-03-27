package ru.aston.service;

import ru.aston.dao.AuthorDao;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

public class AuthorService {
    private final AuthorDao authorDao;

    public AuthorService() {
        this.authorDao = new AuthorDao();
    }

    public AuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }
    public void deleteAuthor(int authorId) {
        authorDao.deleteAuthor(authorId);
    }

    public List<AuthorDto> getAllAuthors() {
        return authorDao.getAllAuthors();
    }

    public AuthorDto createAuthor(AuthorDtoShort authorDtoShort) {
        return authorDao.createAuthor(authorDtoShort);
    }

    public AuthorDto getAuthorById(int authorId) {

        return authorDao.getAuthorById(authorId);
    }
}
