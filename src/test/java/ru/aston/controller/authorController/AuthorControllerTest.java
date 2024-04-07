package ru.aston.controller.authorController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.service.authorService.AuthorServiceImpl;

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
 * Test class for the AuthorController class, which handles HTTP requests related to authors.
 */
@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private AuthorServiceImpl mockAuthorServiceImpl;

    private AuthorController authorController;

    /**
     * Set up the test environment before each test case.
     */
    @BeforeEach
    public void setUp() {
        authorController = new AuthorController(mockAuthorServiceImpl);
    }

    /**
     * Tests the doPost method with a valid author.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoPost_ValidAuthor() throws Exception {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setAuthorId(1);
        authorDto.setAuthorName("Steven King");

        String requestBody = "{\"authorName\": \"Steven King\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockAuthorServiceImpl.createAuthor(any())).thenReturn(authorDto);

        authorController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockAuthorServiceImpl).createAuthor(any());
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doPost method with an invalid author.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoPost_InvalidAuthor() throws Exception {
        String requestBody = "{\"authorName\": \"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        authorController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Author must have a non-null and non-empty authorName");
        verifyNoInteractions(mockAuthorServiceImpl);
    }

    /**
     * Tests the doPost method with an invalid request body.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        String requestBody = "";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        authorController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        verifyNoInteractions(mockAuthorServiceImpl);
    }

    /**
     * Tests the doDelete method with a valid author.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoDelete_ValidAuthor() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/123");

        authorController.doDelete(mockRequest, mockResponse);

        verify(mockAuthorServiceImpl).deleteAuthor(123);
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Tests the doDelete method with an invalid URL.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        authorController.doDelete(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method with an invalid URL.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoGet_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        authorController.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method to get all authors.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoGet_GetAllUsers() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<AuthorDto> authorList = new ArrayList<>();
        authorList.add(new AuthorDto());
        authorList.add(new AuthorDto());
        when(mockAuthorServiceImpl.getAllAuthors()).thenReturn(authorList);

        authorController.doGet(mockRequest, mockResponse);

        verify(mockAuthorServiceImpl).getAllAuthors();
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method to get an author by ID.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoGet_GetAuthorById() throws Exception {
        int authorId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + authorId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        AuthorDto author = new AuthorDto();
        when(mockAuthorServiceImpl.getAuthorById(authorId)).thenReturn(author);

        authorController.doGet(mockRequest, mockResponse);

        verify(mockAuthorServiceImpl).getAuthorById(authorId);
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method when the requested author is not found.
     * @throws Exception if an exception occurs during test execution
     */
    @Test
    public void testDoGet_AuthorNotFound() throws Exception {
        int authorId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + authorId);

        when(mockAuthorServiceImpl.getAuthorById(authorId)).thenReturn(null);

        authorController.doGet(mockRequest, mockResponse);

        verify(mockAuthorServiceImpl).getAuthorById(authorId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
    }
}