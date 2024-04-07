package ru.aston.controller.commentController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.service.commentService.CommentServiceImpl;

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
public class CommentControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private CommentServiceImpl mockCommentServiceImpl;

    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        commentController = new CommentController(mockCommentServiceImpl);
    }

    @Test
    public void testDoPost_ValidComment() throws Exception {
        // Arrange
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setCommentId(1);
        commentShortDto.setText("Nice book");
        commentShortDto.setBookId(1);

        String requestBody = "{\"text\": \"Nice book\", \"bookId\": 1,  \"userId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockCommentServiceImpl.createComment(any())).thenReturn(commentShortDto); // Assume createComment returns true for valid comment

        // Act
        commentController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockCommentServiceImpl).createComment(any());
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidComment() throws Exception {
        // Arrange
        String requestBody = "{\"text\": \"\", \"bookId\": 1,  \"userId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        commentController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Comment must have non-empty comment text, positive bookId and userId");
        // Verify that no interaction with the commentService occurred
        verifyNoInteractions(mockCommentServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        // Arrange
        String requestBody = ""; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        commentController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        // Verify that no interaction with the commentService occurred
        verifyNoInteractions(mockCommentServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoDelete_ValidComment() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/123");

        // Act
        commentController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockCommentServiceImpl).deleteComment(123); // Ensure deleteComment is called with correct comment ID
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        // Act
        commentController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        // Act
        commentController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_GetAllComments() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<CommentDto> commentList = new ArrayList<>();
        commentList.add(new CommentDto());
        commentList.add(new CommentDto());
        when(mockCommentServiceImpl.getAllComments()).thenReturn(commentList);

        // Act
        commentController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockCommentServiceImpl).getAllComments();
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_GetCommentById() throws Exception {
        // Arrange
        int commentId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + commentId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        CommentDto comment = new CommentDto();
        when(mockCommentServiceImpl.getCommentById(commentId)).thenReturn(comment);

        // Act
        commentController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockCommentServiceImpl).getCommentById(commentId);
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_commentNotFound() throws Exception {
        // Arrange
        int commentId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + commentId);

        when(mockCommentServiceImpl.getCommentById(commentId)).thenReturn(null);

        // Act
        commentController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockCommentServiceImpl).getCommentById(commentId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
    }
    
}
