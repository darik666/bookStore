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

/**
 * Servlet implementation for managing book-related HTTP requests.
 * This servlet handles book creation, retrieval, and deletion.
 */
@WebServlet("/books/*")
public class BookController extends HttpServlet {
    /**
     * The BookServiceImpl instance associated with this BookController.
     */
    private final BookServiceImpl bookServiceImpl;

    /**
     * Constructs a new BookController instance with a default BookServiceImpl.
     */
    public BookController() {
        this.bookServiceImpl = new BookServiceImpl();
    }

    /**
     * Constructs a new BookController instance
     * with a provided BookServiceImpl for testing purposes.
     *
     * @param bookServiceImpl The BookServiceImpl instance to be used.
     */
    public BookController(BookServiceImpl bookServiceImpl) {
        this.bookServiceImpl = bookServiceImpl;
    }

    /**
     * Handles HTTP POST requests for creating new book.
     * Expects a JSON request body containing book title and author id.
     * Responds with the created book's details in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Handles HTTP DELETE requests for deleting books.
     * Expects a book ID in the request URL path.
     * Responds with a 204 No Content status upon successful deletion.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Handles HTTP GET requests for retrieving books.
     * Supports retrieving all books or a specific book by ID.
     * Responds with book data in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Retrieves a book by ID and sends it as JSON in the response.
     * If the book is not found, sends a 404 error response.
     *
     * @param resp   HttpServletResponse object to handle the response
     * @param bookId ID of the book to retrieve
     * @throws IOException if an I/O exception occurs
     */
    private void getBookById(HttpServletResponse resp, int bookId) throws IOException {
        try {
            BookDto book = bookServiceImpl.getBookById(bookId);
            if (book != null) {
                sendAsJson(resp, book);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            }
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
        }
    }

    /**
     * Retrieves all books and sends them as JSON in the response.
     *
     * @param resp HttpServletResponse object to handle the response
     * @throws IOException if an I/O exception occurs
     */
    private void getAllBooks(HttpServletResponse resp) throws IOException {
        List<BookDto> books = bookServiceImpl.getAllBooks();
        sendAsJson(resp, books);
    }

    /**
     * Extracts the request body as a BookDtoShort object.
     *
     * @param req HttpServletRequest object representing the request
     * @return Optional with BookDtoShort object if extraction is successful, otherwise - empty Optional
     * @throws IOException if an I/O exception occurs
     */
    private Optional<BookShortDto> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            BookShortDto bookShortDto = mapper.readValue(reader, BookShortDto.class);
            return Optional.of(bookShortDto);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    /**
     * Sends the specified object as JSON in the response.
     *
     * @param response HttpServletResponse object to handle the response
     * @param obj      Object to be serialized to JSON
     * @throws IOException if an I/O exception occurs
     */
    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), obj);
    }
}