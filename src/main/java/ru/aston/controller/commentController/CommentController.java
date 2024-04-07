package ru.aston.controller.commentController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.service.commentService.CommentServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet implementation for managing comment-related HTTP requests.
 * This servlet handles comment creation, retrieval, and deletion.
 */
@WebServlet("/comments/*")
public class CommentController extends HttpServlet {
    /**
     * The CommentServiceImpl instance associated with this CommentController.
     */
    private final CommentServiceImpl commentServiceImpl;

    /**
     * Constructs a new CommentController instance with a default CommentServiceImpl.
     */
    public CommentController() {
        this.commentServiceImpl = new CommentServiceImpl();
    }

    /**
     * Constructs a new CommentController instance
     * with a provided CommentServiceImpl for testing purposes.
     *
     * @param commentServiceImpl The CommentServiceImpl instance to be used.
     */
    public CommentController(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

    /**
     * Handles HTTP POST requests for creating new comments.
     * Expects a JSON request body containing comment text, user id and book id.
     * Responds with the created comment's details in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<CommentShortDto> commentShortDtoOptional = extractRequestBody(req);
        if (commentShortDtoOptional.isPresent()) {
            CommentShortDto commentShortDto = commentShortDtoOptional.get();
            if (commentShortDto.getBookId() <= 0 || commentShortDto.getUserId() <= 0 ||
            commentShortDto.getText() == null || commentShortDto.getText().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Comment must have non-empty comment text, positive bookId and userId");
            } else {
                CommentShortDto createdComment = commentServiceImpl.createComment(commentShortDto);
                sendAsJson(resp, createdComment);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        }
    }

    /**
     * Handles HTTP DELETE requests for deleting comments.
     * Expects a comment ID in the request URL path.
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
        int commentId = Integer.parseInt(pathInfo.substring(1));
        commentServiceImpl.deleteComment(commentId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Handles HTTP GET requests for retrieving comment.
     * Supports retrieving all comment or a specific comment by ID.
     * Responds with comment data in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllComments(resp);
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
                getCommentById(resp, Integer.parseInt(pathParts[1]));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            }
        }
    }

    /**
     * Retrieves a comment by ID and sends it as JSON in the response.
     * If the comment is not found, sends a 404 error response.
     *
     * @param resp   HttpServletResponse object to handle the response
     * @param commentId ID of the comment to retrieve
     * @throws IOException if an I/O exception occurs
     */
    private void getCommentById(HttpServletResponse resp, int commentId) throws IOException {
        CommentDto comment = commentServiceImpl.getCommentById(commentId);
        if (comment != null) {
            sendAsJson(resp, comment);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
        }
    }

    /**
     * Retrieves all comments and sends them as JSON in the response.
     *
     * @param resp HttpServletResponse object to handle the response
     * @throws IOException if an I/O exception occurs
     */
    private void getAllComments(HttpServletResponse resp) throws IOException {
        List<CommentDto> comments = commentServiceImpl.getAllComments();
        sendAsJson(resp, comments);
    }

    /**
     * Extracts the request body as a CommentDtoShort object.
     *
     * @param req HttpServletRequest object representing the request
     * @return Optional with CommentDtoShort object if extraction is successful, otherwise - empty Optional
     * @throws IOException if an I/O exception occurs
     */
    private Optional<CommentShortDto> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            CommentShortDto commentShortDto = mapper.readValue(reader, CommentShortDto.class);
            return Optional.of(commentShortDto);
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