package ru.aston.controller.bookController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.service.bookService.BookServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Test class for the BookController class, which handles HTTP requests related to books.
 */
@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BookServiceImpl mockBookServiceImpl;

    private BookController bookController;

    /**
     * Sets up the test environment before each test method is run.
     */
    @BeforeEach
    public void setUp() {
        bookController = new BookController(mockBookServiceImpl);
    }

    /**
     * Tests the doPost method for creating a valid book.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_ValidBook() throws Exception {
        BookShortDto bookShortDto = new BookShortDto();
        bookShortDto.setBookId(1);
        bookShortDto.setBookTitle("Scary fog");
        bookShortDto.setAuthorId(1);

        String requestBody = "{\"bookTitle\": \"Scary fog\", \"authorId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockBookServiceImpl.createBook(any())).thenReturn(bookShortDto);

        bookController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockBookServiceImpl).createBook(any());
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doPost method for handling an invalid book creation request.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_InvalidBook() throws Exception {
        String requestBody = "{\"bookTitle\": \"\", \"authorId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        bookController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Book must have non-null bookTitle and positive authorId");
        verifyNoInteractions(mockBookServiceImpl);
    }

    /**
     * Tests the doPost method for handling an invalid request body.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        String requestBody = "";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        bookController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        verifyNoInteractions(mockBookServiceImpl);
    }

    /**
     * Tests the doDelete method for deleting a valid book.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoDelete_ValidBook() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/123");

        bookController.doDelete(mockRequest, mockResponse);

        verify(mockBookServiceImpl).deleteBook(123);
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Tests the doDelete method for handling an invalid URL.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        bookController.doDelete(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method for handling an invalid URL.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        bookController.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method for retrieving all books.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetAllBooks() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<BookDto> bookList = new ArrayList<>();
        bookList.add(new BookDto());
        bookList.add(new BookDto());
        when(mockBookServiceImpl.getAllBooks()).thenReturn(bookList);

        bookController.doGet(mockRequest, mockResponse);

        verify(mockBookServiceImpl).getAllBooks();
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for retrieving a book by ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetBookById() throws Exception {
        int bookId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + bookId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        BookDto book = new BookDto();
        when(mockBookServiceImpl.getBookById(bookId)).thenReturn(book);

        bookController.doGet(mockRequest, mockResponse);

        verify(mockBookServiceImpl).getBookById(bookId);
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for handling a book not found scenario.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_bookNotFound() throws Exception {
        int bookId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + bookId);

        when(mockBookServiceImpl.getBookById(bookId)).thenReturn(null);

        bookController.doGet(mockRequest, mockResponse);

        verify(mockBookServiceImpl).getBookById(bookId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
    }
}