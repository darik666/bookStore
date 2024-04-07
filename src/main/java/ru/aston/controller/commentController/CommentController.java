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

@WebServlet("/comments/*")
public class CommentController extends HttpServlet {
    private final CommentServiceImpl commentServiceImpl;

    public CommentController() {
        this.commentServiceImpl = new CommentServiceImpl();
    }

    public CommentController(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

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

    private void getCommentById(HttpServletResponse resp, int commentId) throws IOException {
        CommentDto comment = commentServiceImpl.getCommentById(commentId);
        if (comment != null) {
            sendAsJson(resp, comment);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
        }
    }

    private void getAllComments(HttpServletResponse resp) throws IOException {
        List<CommentDto> comments = commentServiceImpl.getAllComments();
        sendAsJson(resp, comments);
    }

    private Optional<CommentShortDto> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            CommentShortDto commentShortDto = mapper.readValue(reader, CommentShortDto.class);
            return Optional.of(commentShortDto);
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
