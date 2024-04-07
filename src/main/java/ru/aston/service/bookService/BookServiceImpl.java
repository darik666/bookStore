package ru.aston.service.bookService;

import ru.aston.dao.bookDao.BookDaoImpl;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

public class BookServiceImpl implements BookService {
    private final BookDaoImpl bookDaoImpl;

    public BookServiceImpl() {
        this.bookDaoImpl = new BookDaoImpl();
    }

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
