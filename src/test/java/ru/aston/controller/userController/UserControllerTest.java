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

/**
 * Test class for the UserController class, which handles HTTP requests related to users.
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private UserServiceImpl mockUserServiceImpl;

    private UserController userController;

    /**
     * Sets up the test environment before each test method is run.
     */
    @BeforeEach
    public void setUp() {
        userController = new UserController(mockUserServiceImpl);
    }

    /**
     * Tests the doPost method for creating a valid user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_ValidUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setUserName("Jameson");

        String requestBody = "{\"name\": \"Jameson\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        PrintWriter writer = new PrintWriter(new StringWriter());

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockResponse.getWriter()).thenReturn(writer);
        when(mockUserServiceImpl.createUser(any())).thenReturn(userDto);

        userController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockUserServiceImpl).createUser(any());
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doPost method for handling an invalid user creation request.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_InvalidUser() throws Exception {
        String requestBody = "{\"name\": \"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        userController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST,
                "User must have a non-null and non-empty userName");
        verifyNoInteractions(mockUserServiceImpl);
    }

    /**
     * Tests the doPost method for handling an invalid request body.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoPost_InvalidRequestBody() throws Exception {
        String requestBody = "";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));

        when(mockRequest.getReader()).thenReturn(reader);

        userController.doPost(mockRequest, mockResponse);

        verify(mockRequest).getReader();
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        verifyNoInteractions(mockUserServiceImpl);
    }

    /**
     * Tests the doDelete method for deleting a valid user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoDelete_ValidUser() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/123");

        userController.doDelete(mockRequest, mockResponse);

        verify(mockUserServiceImpl).deleteUser(123);
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Tests the doDelete method for handling an invalid URL.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoDelete_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        userController.doDelete(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method for handling an invalid URL.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_InvalidURL() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        userController.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
    }

    /**
     * Tests the doGet method for retrieving all users.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetAllUsers() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/");
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        List<UserDto> userList = new ArrayList<>();
        userList.add(new UserDto());
        userList.add(new UserDto());
        when(mockUserServiceImpl.getAllUsers()).thenReturn(userList);

        userController.doGet(mockRequest, mockResponse);

        verify(mockUserServiceImpl).getAllUsers();
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for retrieving a user by ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_GetUserById() throws Exception {
        int userId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + userId);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(mockResponse.getWriter()).thenReturn(writer);

        UserDto user = new UserDto();
        when(mockUserServiceImpl.getUserById(userId)).thenReturn(user);

        userController.doGet(mockRequest, mockResponse);

        verify(mockUserServiceImpl).getUserById(userId);
        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Tests the doGet method for handling a user not found scenario.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testDoGet_UserNotFound() throws Exception {
        int userId = 123;
        when(mockRequest.getPathInfo()).thenReturn("/" + userId);
        when(mockUserServiceImpl.getUserById(userId)).thenReturn(null);

        userController.doGet(mockRequest, mockResponse);

        verify(mockUserServiceImpl).getUserById(userId);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
    }
}