package ru.aston.service;

import org.springframework.stereotype.Service;
import ru.aston.dao.AuthorDao;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

@Service
public class AuthorService {
    private AuthorDao authorDao;

    public AuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }
    public void deleteAuthor(int authorId) {
        authorDao.deleteAuthor(authorId);
    }

    public List<AuthorDto> getAllAuthors() {
        return authorDao.getAllAuthors();
    }

    public AuthorDto createUser(AuthorDtoShort authorDtoShort) {
        return authorDao.createAuthor(authorDtoShort);
    }

    public AuthorDto getAuthorById(int authorId) {

        return authorDao.getAuthorById(authorId);
    }
}
