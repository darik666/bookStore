package ru.aston.controller.userController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.service.userService.UserServiceImpl;
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
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private UserServiceImpl mockUserServiceImpl;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController(mockUserServiceImpl);
    }

    @Test
    public void testDoPost_ValidUser() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setUserName("Jameson");

        String requestBody = "{\"name\": \"Jameson\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockUserServiceImpl.createUser(any())).thenReturn(userDto); // Assume createUser returns true for valid user

        // Act
        userController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockUserServiceImpl).createUser(any());
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidUser() throws Exception {
        // Arrange
        String requestBody = "{\"name\": \"\"}"; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        userController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "User must have a non-null and non-empty userName");
        // Verify that no interaction with the UserService occurred
        verifyNoInteractions(mockUserServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        // Arrange
        String requestBody = ""; // Invalid request body
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);

        // Act
        userController.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        // Verify that no interaction with the UserService occurred
        verifyNoInteractions(mockUserServiceImpl);
        // Add more assertions based on your response
    }

    @Test
    public void testDoDelete_ValidUser() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/123"); // Assuming user ID is 123
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        userController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockUserServiceImpl).deleteUser(123); // Ensure deleteUser is called with correct user ID
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        userController.doDelete(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_InvalidURL() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/invalid");
        PrintWriter writer = new PrintWriter(new StringWriter());

        // Act
        userController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    @Test
    public void testDoGet_GetAllUsers() throws Exception {
        // Arrange
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<UserDto> userList = new ArrayList<>();
        userList.add(new UserDto());
        userList.add(new UserDto());
        when(mockUserServiceImpl.getAllUsers()).thenReturn(userList);

        // Act
        userController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockUserServiceImpl).getAllUsers();
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_GetUserById() throws Exception {
        // Arrange
        int userId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + userId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        UserDto user = new UserDto();
        when(mockUserServiceImpl.getUserById(userId)).thenReturn(user);

        // Act
        userController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockUserServiceImpl).getUserById(userId);
        verify(mockResponse).setContentType("application/json");
        // Add more assertions based on the response content
    }

    @Test
    public void testDoGet_UserNotFound() throws Exception {
        // Arrange
        int userId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + userId);
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockUserServiceImpl.getUserById(userId)).thenReturn(null);

        // Act
        userController.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockUserServiceImpl).getUserById(userId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
    }


}
