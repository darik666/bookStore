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

/**
 * Test class for the CommentController class, which handles HTTP requests related to comments.
 */
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private CommentServiceImpl mockCommentServiceImpl;

    private CommentController commentController;

    /**
     * Sets up the test environment before each test method is run.
     */
    @BeforeEach
    public void setUp() {
        commentController = new CommentController(mockCommentServiceImpl);
    }

    /**
     * Tests the doPost method for creating a valid comment.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_ValidComment() throws Exception {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setCommentId(1);
        commentShortDto.setText("Nice book");
        commentShortDto.setBookId(1);

        String requestBody = "{\"text\": \"Nice book\", \"bookId\": 1,  \"userId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockCommentServiceImpl.createComment(any())).thenReturn(commentShortDto);

        commentController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockCommentServiceImpl).createComment(any());
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doPost method for handling an invalid comment creation request.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_InvalidComment() throws Exception {
        String requestBody = "{\"text\": \"\", \"bookId\": 1,  \"userId\": 1}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        commentController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Comment must have non-empty comment text, positive bookId and userId");
        verifyNoInteractions(mockCommentServiceImpl);
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

        commentController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        verifyNoInteractions(mockCommentServiceImpl);
    }

    /**
     * Tests the doDelete method for deleting a valid comment.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoDelete_ValidComment() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/123");

        commentController.doDelete(mockRequest, mockResponse);

        verify(mockCommentServiceImpl).deleteComment(123);
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

        commentController.doDelete(mockRequest, mockResponse);

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

        commentController.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method for retrieving all comments.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetAllComments() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<CommentDto> commentList = new ArrayList<>();
        commentList.add(new CommentDto());
        commentList.add(new CommentDto());
        when(mockCommentServiceImpl.getAllComments()).thenReturn(commentList);

        commentController.doGet(mockRequest, mockResponse);

        verify(mockCommentServiceImpl).getAllComments();
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for retrieving a comment by ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetCommentById() throws Exception {
        int commentId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + commentId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        CommentDto comment = new CommentDto();
        when(mockCommentServiceImpl.getCommentById(commentId)).thenReturn(comment);

        commentController.doGet(mockRequest, mockResponse);

        verify(mockCommentServiceImpl).getCommentById(commentId);
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for handling a comment not found scenario.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_commentNotFound() throws Exception {
        int commentId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + commentId);

        when(mockCommentServiceImpl.getCommentById(commentId)).thenReturn(null);

        commentController.doGet(mockRequest, mockResponse);

        verify(mockCommentServiceImpl).getCommentById(commentId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
    }
}