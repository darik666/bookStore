package ru.aston.controller.commentController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dao.CommentDao;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.service.CommentService;

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

@WebServlet("/comments/*")
public class CommentController extends HttpServlet {
    private final CommentService commentService;

    public CommentController() {
        this.commentService = new CommentService();
    }

    public CommentController(DataSource dataSource) {
        this.commentService = new CommentService(new CommentDao(dataSource));
    }

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

    private void getAllComments(HttpServletResponse resp) throws IOException {
        List<CommentDto> comments = commentService.getAllComments();
        sendAsJson(resp, comments);
    }

    private void getCommentById(HttpServletResponse resp, int commentId) throws IOException {
        CommentDto comment = commentService.getCommentById(commentId);
        if (comment != null) {
            sendAsJson(resp, comment);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = extractRequestBody(req);
        CommentShortDto commentShortDto = parseCommentShortDto(requestBody);
        String commentText = commentShortDto.getText();
        if (commentText == null || commentText.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Comment must have a non-null and non-empty text");
        } else {
            CommentShortDto createdComment = commentService.createComment(commentShortDto);
            sendAsJson(resp, createdComment);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int commentId = Integer.parseInt(pathInfo.substring(1));
        commentService.deleteComment(commentId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private CommentShortDto parseCommentShortDto(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(requestBody, CommentShortDto.class);
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
