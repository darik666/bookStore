package ru.aston.controller.bookController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.service.BookService;
import ru.aston.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService; // Assuming you have a BookService to handle user-related operations

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService; // Initialize BookService according to your implementation
    }

    @GetMapping("/{bookId}")
    public BookDto getBook(@PathVariable int bookId) {
        return bookService.getBookById(bookId);
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public BookShortDto createBook(@RequestBody BookShortDto bookShortDto) {
        return bookService.createBook(bookShortDto);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable int bookId) {
        bookService.deleteBook(bookId);
    }
}

