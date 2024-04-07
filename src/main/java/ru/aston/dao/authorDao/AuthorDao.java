package ru.aston.dao.authorDao;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

/**
 * Interface representing operations for interacting with author data.
 */
public interface AuthorDao {

    /**
     * Creates a new author based on the provided AuthorDtoShort object.
     *
     * @param authorDtoShort The AuthorDtoShort object containing author data.
     * @return The created AuthorDto object.
     */
    AuthorDto createAuthor(AuthorDtoShort authorDtoShort);

    /**
     * Deletes an author with the specified ID.
     *
     * @param authorId The ID of the author to delete.
     */
    void deleteAuthor(int authorId);

    /**
     * Retrieves an author by ID.
     *
     * @param authorId The ID of the author to retrieve.
     * @return The AuthorDto object representing the retrieved author, or null if not found.
     */
    AuthorDto getAuthorById(int authorId);

    /**
     * Retrieves all authors.
     *
     * @return A list of AuthorDto objects representing all authors.
     */
    List<AuthorDto> getAllAuthors();
}