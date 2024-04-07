package ru.aston.controller.bookController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.service.bookService.BookServiceImpl;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/books/*")
public class BookController extends HttpServlet {
    private final BookServiceImpl bookServiceImpl; // Assuming you have a BookService to handle user-related operations

    public BookController() {
        this.bookServiceImpl = new BookServiceImpl();
    }

    public BookController(BookServiceImpl bookServiceImpl) {
        this.bookServiceImpl = bookServiceImpl; // Initialize BookService according to your implementation
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<BookShortDto> bookShortDtoOptional = extractRequestBody(req);
        if (bookShortDtoOptional.isPresent()) {
           BookShortDto bookShortDto = bookShortDtoOptional.get();
           if (bookShortDto.getBookTitle() == null || bookShortDto.getBookTitle().isBlank() ||
                   bookShortDto.getAuthorId() <= 0) {
               resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                       "Book must have non-null bookTitle and positive authorId");
           } else {
               BookShortDto createdBook = bookServiceImpl.createBook(bookShortDto);
               sendAsJson(resp, createdBook);
           }
        } else {
               resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
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
        bookServiceImpl.deleteBook(bookId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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

    private void getBookById(HttpServletResponse resp, int bookId) throws IOException {
        BookDto book = bookServiceImpl.getBookById(bookId);
        if (book != null) {
            sendAsJson(resp, book);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
        }
    }

    private void getAllBooks(HttpServletResponse resp) throws IOException {
        List<BookDto> books = bookServiceImpl.getAllBooks();
        sendAsJson(resp, books);
    }

    private Optional<BookShortDto> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            BookShortDto bookShortDto = mapper.readValue(reader, BookShortDto.class);
            return Optional.of(bookShortDto);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), obj);
    }
}

