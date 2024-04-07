package ru.aston.service.authorService;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;

import java.util.List;

/**
 * The interface defining operations related to author management.
 */
public interface AuthorService {

    /**
     * Creates a new author.
     *
     * @param authorDtoShort An AuthorDtoShort object containing information about the new author.
     * @return The created AuthorDto object.
     */
    AuthorDto createAuthor(AuthorDtoShort authorDtoShort);

    /**
     * Deletes an author by their ID.
     *
     * @param authorId The ID of the author to delete.
     */
    void deleteAuthor(int authorId);

    /**
     * Retrieves a list of all authors.
     *
     * @return A list of AuthorDto objects representing all authors.
     */
    List<AuthorDto> getAllAuthors();

    /**
     * Retrieves an author by their ID.
     *
     * @param authorId The ID of the author to retrieve.
     * @return The AuthorDto object representing the author with the specified ID.
     */
    AuthorDto getAuthorById(int authorId);
}