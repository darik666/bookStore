package ru.aston.controller.bookController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dao.BookDao;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.service.BookService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/books/*")
public class BookController extends HttpServlet {
    private final BookService bookService; // Assuming you have a BookService to handle user-related operations

    public BookController() {
        this.bookService = new BookService();
    }

    public BookController(DataSource dataSource) {
        this.bookService = new BookService(new BookDao(dataSource)); // Initialize BookService according to your implementation
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllBooks(resp);
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
                getBookById(resp, Integer.parseInt(pathParts[1]));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            }
        }
    }

    private void getAllBooks(HttpServletResponse resp) throws IOException {
        List<BookDto> books = bookService.getAllBooks();
        sendAsJson(resp, books);
    }

    private void getBookById(HttpServletResponse resp, int bookId) throws IOException {
        BookDto book = bookService.getBookById(bookId);
        if (book != null) {
            sendAsJson(resp, book);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = extractRequestBody(req);
        BookShortDto bookShortDto = parseBookDto(requestBody);
        String bookTitle = bookShortDto.getBookTitle();
        if (bookTitle == null || bookTitle.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Book must have a non-null and non-empty bookTitle");
        } else {
            BookShortDto createdBook = bookService.createBook(bookShortDto);
            sendAsJson(resp, createdBook);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int bookId = Integer.parseInt(pathInfo.substring(1));
        bookService.deleteBook(bookId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private BookShortDto parseBookDto(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(requestBody, BookShortDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid request body", e);
        }
    }

    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), obj);
    }
}

