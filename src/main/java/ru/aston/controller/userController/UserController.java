package ru.aston.controller.userController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dao.UserDao;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.service.UserService;
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

@WebServlet("/users/*")
public class UserController extends HttpServlet {
    private final UserService userService; // Assuming you have a UserService to handle user-related operations

    public UserController() {
        this.userService = new UserService();
    }

    public UserController(DataSource dataSource) {
        this.userService = new UserService(new UserDao(dataSource));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    private void getUserById(HttpServletResponse resp, int userId) throws IOException {
        UserDto user = userService.getUserById(userId);
        if (user != null) {
            sendAsJson(resp, user);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    private void getAllUsers(HttpServletResponse resp) throws IOException {
        List<UserDto> users = userService.getAllUsers();
        sendAsJson(resp, users);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = extractRequestBody(req);
        UserDtoShort userDtoShort = parseUserDtoShort(requestBody);
        String userName = userDtoShort.getName();
        if (userName == null || userName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User must have a non-null and non-empty userName");
        } else {
            UserDto createdUser = userService.createUser(userDtoShort);
            sendAsJson(resp, createdUser);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int userId = Integer.parseInt(pathInfo.substring(1));
        userService.deleteUser(userId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }


    private String extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private UserDtoShort parseUserDtoShort(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(requestBody, UserDtoShort.class);
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