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


@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private AuthorServiceImpl mockAuthorServiceImpl;

    private AuthorController authorController;

    @BeforeEach
    public void setUp() {
        authorController = new AuthorController(mockAuthorServiceImpl);
    }

    @Test
    public void testDoPost_ValidAuthor() throws Exception {
        // Arrange
        AuthorDto authorDto = new AuthorDto();
        authorDto.setAuthorId(1);
        authorDto.setAuthorName("Steven King");

        String requestBody = "{\"authorName\": \"Steven King\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockAuthorServiceImpl.createAuthor(any())).thenReturn(authorDto); // Assume createAuthor returns true for valid author

        // Act
        authorController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockAuthorServiceImpl).createAuthor(any());
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidAuthor() throws Exception {
        // Arrange
        String requestBody = "{\"authorName\": \"\"}"; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        authorController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Author must have a non-null and non-empty authorName");
        // Verify that no interaction with the AuthorService occurred
        verifyNoInteractions(mockAuthorServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        // Arrange
        String requestBody = ""; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        authorController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        // Verify that no interaction with the AuthorService occurred
        verifyNoInteractions(mockAuthorServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoDelete_ValidAuthor() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/123");
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        authorController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockAuthorServiceImpl).deleteAuthor(123); // Ensure deleteAuthor is called with correct author ID
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        authorController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        authorController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_GetAllUsers() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<AuthorDto> authorList = new ArrayList<>();
        authorList.add(new AuthorDto());
        authorList.add(new AuthorDto());
        when(mockAuthorServiceImpl.getAllAuthors()).thenReturn(authorList);

        // Act
        authorController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockAuthorServiceImpl).getAllAuthors();
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_GetAuthorById() throws Exception {
        // Arrange
        int authorId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + authorId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        AuthorDto author = new AuthorDto();
        when(mockAuthorServiceImpl.getAuthorById(authorId)).thenReturn(author);

        // Act
        authorController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockAuthorServiceImpl).getAuthorById(authorId);
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_AuthorNotFound() throws Exception {
        // Arrange
        int authorId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + authorId);
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockAuthorServiceImpl.getAuthorById(authorId)).thenReturn(null);

        // Act
        authorController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockAuthorServiceImpl).getAuthorById(authorId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
    }

}
