package ru.aston.controller.userController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.service.userService.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/users/*")
public class UserController extends HttpServlet {
    private final UserServiceImpl userServiceImpl;

    public UserController() {
        this.userServiceImpl = new UserServiceImpl();
    }

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<UserDtoShort> userDtoShortOptional = extractRequestBody(req);
        if (userDtoShortOptional.isPresent()) {
            UserDtoShort userDtoShort = userDtoShortOptional.get();
            if (userDtoShort.getName() == null || userDtoShort.getName().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User must have a non-null and non-empty userName");
            } else {
                UserDto createdUser = userServiceImpl.createUser(userDtoShort);
                sendAsJson(resp, createdUser);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        int userId = Integer.parseInt(pathInfo.substring(1));
        userServiceImpl.deleteUser(userId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        UserDto user = userServiceImpl.getUserById(userId);
        if (user != null) {
            sendAsJson(resp, user);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    private void getAllUsers(HttpServletResponse resp) throws IOException {
        List<UserDto> users = userServiceImpl.getAllUsers();
        sendAsJson(resp, users);
    }

    private Optional<UserDtoShort> extractRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            UserDtoShort userDtoShort = mapper.readValue(reader, UserDtoShort.class);
            return Optional.of(userDtoShort);
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