package ru.aston.controller.userController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.service.userService.UserServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet implementation for managing user-related HTTP requests.
 * This servlet handles user creation, retrieval, and deletion.
 */
@WebServlet("/users/*")
public class UserController extends HttpServlet {
    /**
     * The UserServiceImpl instance associated with this UserController.
     */
    private final UserServiceImpl userServiceImpl;

    /**
     * Constructs a new UserController instance with a default UserServiceImpl.
     */
    public UserController() {
        this.userServiceImpl = new UserServiceImpl();
    }

    /**
     * Constructs a new UserController instance
     * with a provided UserServiceImpl for testing purposes.
     *
     * @param userServiceImpl The UserServiceImpl instance to be used.
     */
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    /**
     * Handles HTTP POST requests for creating new users.
     * Expects a JSON request body containing username.
     * Responds with the created user's details in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<UserDtoShort> userDtoShortOptional = extractRequestBody(req);
        if (userDtoShortOptional.isPresent()) {
            UserDtoShort userDtoShort = userDtoShortOptional.get();
            if (userDtoShort.getName() == null || userDtoShort.getName().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "User must have a non-null and non-empty userName");
            } else {
                UserDto createdUser = userServiceImpl.createUser(userDtoShort);
                sendAsJson(resp, createdUser);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        }
    }

    /**
     * Handles HTTP DELETE requests for deleting users.
     * Expects a user ID in the request URL path.
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
        int userId = Integer.parseInt(pathInfo.substring(1));
        userServiceImpl.deleteUser(userId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Handles HTTP GET requests for retrieving users.
     * Supports retrieving all users or a specific user by ID.
     * Responds with user data in JSON format.
     *
     * @param req  The HttpServletRequest object representing the request.
     * @param resp The HttpServletResponse object representing the response.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllUsers(resp);
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
                getUserById(resp, Integer.parseInt(pathParts[1]));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            }
        }
    }

    /**
     * Retrieves a user by ID and sends it as JSON in the response.
     * If the user is not found, sends a 404 error response.
     *
     * @param resp   HttpServletResponse object to handle the response
     * @param userId ID of the user to retrieve
     * @throws IOException if an I/O exception occurs
     */
    private void getUserById(HttpServletResponse resp, int userId) throws IOException {
        UserDto user = userServiceImpl.getUserById(userId);
        if (user != null) {
            sendAsJson(resp, user);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    /**
     * Retrieves all users and sends them as JSON in the response.
     *
     * @param resp HttpServletResponse object to handle the response
     * @throws IOException if an I/O exception occurs
     */
    private void getAllUsers(HttpServletResponse resp) throws IOException {
        List<UserDto> users = userServiceImpl.getAllUsers();
        sendAsJson(resp, users);
    }

    /**
     * Extracts the request body as a UserDtoShort object.
     *
     * @param req HttpServletRequest object representing the request
     * @return Optional with UserDtoShort object if extraction is successful, otherwise - empty Optional
     * @throws IOException if an I/O exception occurs
     */
    private Optional<UserDtoShort> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            UserDtoShort userDtoShort = mapper.readValue(reader, UserDtoShort.class);
            return Optional.of(userDtoShort);
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