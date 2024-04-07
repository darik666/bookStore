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

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BookServiceImpl mockBookServiceImpl;

    private BookController bookController;

    @BeforeEach
    public void setUp() {
        bookController = new BookController(mockBookServiceImpl);
    }

    @Test
    public void testDoPost_ValidBook() throws Exception {
        // Arrange
        BookShortDto bookShortDto = new BookShortDto();
        bookShortDto.setBookId(1);
        bookShortDto.setBookTitle("Scary fog");
        bookShortDto.setAuthorId(1);

        String requestBody = "{\"bookTitle\": \"Scary fog\", \"authorId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockBookServiceImpl.createBook(any())).thenReturn(bookShortDto); // Assume createBook returns true for valid book

        // Act
        bookController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockBookServiceImpl).createBook(any());
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidBook() throws Exception {
        // Arrange
        String requestBody = "{\"bookTitle\": \"\", \"authorId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        bookController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Book must have non-null bookTitle and positive authorId");
        // Verify that no interaction with the bookService occurred
        verifyNoInteractions(mockBookServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        // Arrange
        String requestBody = ""; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        bookController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        // Verify that no interaction with the bookService occurred
        verifyNoInteractions(mockBookServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoDelete_ValidBook() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/123");

        // Act
        bookController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockBookServiceImpl).deleteBook(123); // Ensure deleteBook is called with correct book ID
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        // Act
        bookController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        // Act
        bookController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_GetAllBooks() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<BookDto> bookList = new ArrayList<>();
        bookList.add(new BookDto());
        bookList.add(new BookDto());
        when(mockBookServiceImpl.getAllBooks()).thenReturn(bookList);

        // Act
        bookController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockBookServiceImpl).getAllBooks();
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_GetBookById() throws Exception {
        // Arrange
        int bookId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + bookId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        BookDto book = new BookDto();
        when(mockBookServiceImpl.getBookById(bookId)).thenReturn(book);

        // Act
        bookController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockBookServiceImpl).getBookById(bookId);
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_bookNotFound() throws Exception {
        // Arrange
        int bookId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + bookId);

        when(mockBookServiceImpl.getBookById(bookId)).thenReturn(null);

        // Act
        bookController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockBookServiceImpl).getBookById(bookId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
    }
}
