package ru.aston.service.bookService;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

/**
 * The interface defining operations related to book management.
 */
public interface BookService {

    /**
     * Creates a new book based on the provided book data.
     * @param bookShortDto The user data.
     * @return The created book.
     */
    BookShortDto createBook(BookShortDto bookShortDto);

    /**
     * Deletes book with the specified ID.
     * @param bookId The ID of the book to delete.
     */
    void deleteBook(int bookId);

    /**
     * Retrieves a list of all books.
     * @return A list of book DTOs.
     */
    List<BookDto> getAllBooks();

    /**
     * Retrieves a book by their ID.
     * @param bookId The ID of the book to retrieve.
     * @return The book DTO.
     */
    BookDto getBookById(int bookId);
}