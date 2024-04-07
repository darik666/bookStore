package ru.aston.service.authorService;

import ru.aston.dao.authorDao.AuthorDaoImpl;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

/**
 * Implementation of interface defining operations related to author management.
 */
public class AuthorServiceImpl implements AuthorService {

    /**
     * The data access object for interacting with author data in the database.
     */
    private final AuthorDaoImpl authorDaoImpl;

    /**
     * Constructs a new AuthorServiceImpl instance for regular application use.
     */
    public AuthorServiceImpl() {
        this.authorDaoImpl = new AuthorDaoImpl();
    }

    /**
     * Constructs a new AuthorServiceImpl instance for testing purposes.
     *
     * @param authorDaoImpl The AuthorDaoImpl instance to be used.
     */
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