package ru.aston.controller.authorController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dao.AuthorDao;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.service.AuthorService;
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

@WebServlet("/authors/*")
public class AuthorController extends HttpServlet {
    private final AuthorService authorService;

    public AuthorController() {
        this.authorService = new AuthorService();
    }

    public AuthorController(DataSource dataSource) {
        this.authorService = new AuthorService(new AuthorDao(dataSource));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    private void getAllAuthors(HttpServletResponse resp) throws IOException {
        List<AuthorDto> authors = authorService.getAllAuthors();
        sendAsJson(resp, authors);
    }

    private void getAuthorById(HttpServletResponse resp, int authorId) throws IOException {
        AuthorDto author = authorService.getAuthorById(authorId);
        if (author != null) {
            sendAsJson(resp, author);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = extractRequestBody(req);
        AuthorDtoShort authorDtoShort = parseAuthorDtoShort(requestBody);
        String authorName = authorDtoShort.getAuthorName();
        if (authorName == null || authorName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Author must have a non-null and non-empty authorName");
        } else {
            AuthorDto createdAuthor = authorService.createAuthor(authorDtoShort);
            sendAsJson(resp, createdAuthor);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int authorId = Integer.parseInt(pathInfo.substring(1));
        authorService.deleteAuthor(authorId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private AuthorDtoShort parseAuthorDtoShort(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(requestBody, AuthorDtoShort.class);
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
