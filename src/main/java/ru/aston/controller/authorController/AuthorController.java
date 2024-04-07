package ru.aston.controller.authorController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.service.authorService.AuthorServiceImpl;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/authors/*")
public class AuthorController extends HttpServlet {
    private final AuthorServiceImpl authorServiceImpl;

    public AuthorController() {
        this.authorServiceImpl = new AuthorServiceImpl();
    }

    public AuthorController(AuthorServiceImpl authorServiceImpl) {
        this.authorServiceImpl = authorServiceImpl;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<AuthorDtoShort> authorDtoShortOptional = extractRequestBody(req);
        if (authorDtoShortOptional.isPresent()) {
            AuthorDtoShort authorDtoShort = authorDtoShortOptional.get();
            if (authorDtoShort.getAuthorName() == null || authorDtoShort.getAuthorName().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author must have a non-null and non-empty authorName");
            } else {
                AuthorDto createdAuthor = authorServiceImpl.createAuthor(authorDtoShort);
                sendAsJson(resp, createdAuthor);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int authorId = Integer.parseInt(pathInfo.substring(1));
        authorServiceImpl.deleteAuthor(authorId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllAuthors(resp);
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
                getAuthorById(resp, Integer.parseInt(pathParts[1]));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            }
        }
    }

    private void getAuthorById(HttpServletResponse resp, int authorId) throws IOException {
        AuthorDto author = authorServiceImpl.getAuthorById(authorId);
        if (author != null) {
            sendAsJson(resp, author);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
        }
    }

    private void getAllAuthors(HttpServletResponse resp) throws IOException {
        List<AuthorDto> authors = authorServiceImpl.getAllAuthors();
        sendAsJson(resp, authors);
    }

    private Optional<AuthorDtoShort> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            AuthorDtoShort authorDtoShort = mapper.readValue(reader, AuthorDtoShort.class);
            return Optional.of(authorDtoShort);
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