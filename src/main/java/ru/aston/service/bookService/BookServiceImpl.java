package ru.aston.service.bookService;

import ru.aston.dao.bookDao.BookDaoImpl;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

/**
 * Implementation of interface defining operations related to book management.
 */
public class BookServiceImpl implements BookService {

    /**
     * The data access object for interacting with book data in the database.
     */
    private final BookDaoImpl bookDaoImpl;

    /**
     * Constructs a new BookServiceImpl instance for regular application use.
     */
    public BookServiceImpl() {
        this.bookDaoImpl = new BookDaoImpl();
    }

    /**
     * Constructs a new BookServiceImpl instance for testing purposes.
     *
     * @param bookDaoImpl The BookDaoImpl instance to be used.
     */
    public BookServiceImpl(BookDaoImpl bookDaoImpl) {
        this.bookDaoImpl = bookDaoImpl;
    }

    public List<BookDto> getAllBooks() {
        return bookDaoImpl.getAllBooks();
    }

    public BookDto getBookById(int bookId) {
        return bookDaoImpl.getBookById(bookId);
    }

    public BookShortDto createBook(BookShortDto bookShortDto) {
        return bookDaoImpl.createBook(bookShortDto);
    }

    public void deleteBook(int bookId) {
        bookDaoImpl.deleteBook(bookId);
    }
}