package ru.aston.dao.bookDao;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

public interface BookDao {

    BookDto getBookById(int bookId);

    List<BookDto> getAllBooks();

    BookShortDto createBook(BookShortDto bookShortDto);

    void deleteBook(int bookId);
}
