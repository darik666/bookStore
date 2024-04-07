package ru.aston.service.bookService;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;

import java.util.List;

public interface BookService {

    List<BookDto> getAllBooks();

    BookDto getBookById(int bookId);

    BookShortDto createBook(BookShortDto bookShortDto);

    void deleteBook(int bookId);
}
