package ru.aston.dao.bookDao;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

/**
 * Interface representing operations for interacting with book data.
 */
public interface BookDao {

    /**
     * Creates a new book based on the provided BookDtoShort object.
     *
     * @param bookShortDto The BookShortDto object containing book data.
     * @return The created BookDto object.
     */
    BookShortDto createBook(BookShortDto bookShortDto);

    /**
     * Deletes a book with the specified ID.
     *
     * @param bookId The ID of the book to delete.
     */
    void deleteBook(int bookId);

    /**
     * Retrieves a book by ID.
     *
     * @param bookId The ID of the book to retrieve.
     * @return The BookDto object representing the retrieved book, or null if not found.
     */
    BookDto getBookById(int bookId);

    /**
     * Retrieves all books.
     *
     * @return A list of BookDto objects representing all books.
     */
    List<BookDto> getAllBooks();
}