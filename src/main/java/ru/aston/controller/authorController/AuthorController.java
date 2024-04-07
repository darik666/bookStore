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

/**
 * Servlet implementation for managing author-related HTTP requests.
 * This servlet handles author creation, retrieval, and deletion.
 */
@WebServlet("/authors/*")
public class AuthorController extends HttpServlet {
    /**
     * The AuthorServiceImpl instance associated with this AuthorController.
     */
    private final AuthorServiceImpl authorServiceImpl;

    /**
     * Constructs a new AuthorController instance with a default AuthorServiceImpl.
     */
    public AuthorController() {
        this.authorServiceImpl = new AuthorServiceImpl();
    }

    /**
     * Constructs a new AuthorController instance
     * with a provided AuthorServiceImpl for testing purposes.
     *
     * @param authorServiceImpl The AuthorServiceImpl instance to be used.
     */
    public AuthorController(AuthorServiceImpl authorServiceImpl) {
        this.authorServiceImpl = authorServiceImpl;
    }

    /**
     * Handles HTTP POST requests for creating new authors.
     * Expects a JSON request body containing author name.
     * Responds with the created author's details in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Handles HTTP DELETE requests for deleting authors.
     * Expects an author ID in the request URL path.
     * Responds with a 204 No Content status upon successful deletion.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Handles HTTP GET requests for retrieving authors.
     * Supports retrieving all authors or a specific author by ID.
     * Responds with author data in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
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

    /**
     * Retrieves an author by ID and sends it as JSON in the response.
     * If the author is not found, sends a 404 error response.
     *
     * @param resp   HttpServletResponse object to handle the response
     * @param authorId ID of the author to retrieve
     * @throws IOException if an I/O exception occurs
     */
    private void getAuthorById(HttpServletResponse resp, int authorId) throws IOException {
        AuthorDto author = authorServiceImpl.getAuthorById(authorId);
        if (author != null) {
            sendAsJson(resp, author);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
        }
    }

    /**
     * Retrieves all authors and sends them as JSON in the response.
     *
     * @param resp HttpServletResponse object to handle the response
     * @throws IOException if an I/O exception occurs
     */
    private void getAllAuthors(HttpServletResponse resp) throws IOException {
        List<AuthorDto> authors = authorServiceImpl.getAllAuthors();
        sendAsJson(resp, authors);
    }

    /**
     * Extracts the request body as a AuthorDtoShort object.
     *
     * @param req HttpServletRequest object representing the request
     * @return Optional with AuthorDtoShort object if extraction is successful, otherwise - empty Optional
     * @throws IOException if an I/O exception occurs
     */
    private Optional<AuthorDtoShort> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            AuthorDtoShort authorDtoShort = mapper.readValue(reader, AuthorDtoShort.class);
            return Optional.of(authorDtoShort);
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