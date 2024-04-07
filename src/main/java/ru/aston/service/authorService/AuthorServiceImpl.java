package ru.aston.service.authorService;

import ru.aston.dao.authorDao.AuthorDaoImpl;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

public class AuthorServiceImpl implements AuthorService {
    private final AuthorDaoImpl authorDaoImpl;

    public AuthorServiceImpl() {
        this.authorDaoImpl = new AuthorDaoImpl();
    }

    public AuthorServiceImpl(AuthorDaoImpl authorDaoImpl) {
        this.authorDaoImpl = authorDaoImpl;
    }

    public void deleteAuthor(int authorId) {
        authorDaoImpl.deleteAuthor(authorId);
    }

    public List<AuthorDto> getAllAuthors() {
        return authorDaoImpl.getAllAuthors();
    }

    public AuthorDto createAuthor(AuthorDtoShort authorDtoShort) {
        return authorDaoImpl.createAuthor(authorDtoShort);
    }

    public AuthorDto getAuthorById(int authorId) {

        return authorDaoImpl.getAuthorById(authorId);
    }
}
