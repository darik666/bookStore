package ru.aston.service;

import ru.aston.dao.BookDao;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

public class BookService {
    private final BookDao bookDao;

    public BookService() {
        this.bookDao = new BookDao();
    }

    public BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public List<BookDto> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public BookDto getBookById(int bookId) {
        return bookDao.getBookById(bookId);
    }

    public BookShortDto createBook(BookShortDto bookShortDto) {
        return bookDao.createBook(bookShortDto);
    }

    public void deleteBook(int bookId) {
        bookDao.deleteBook(bookId);
    }
}
