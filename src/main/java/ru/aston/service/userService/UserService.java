package ru.aston.service.userService;

import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

/**
 * The interface defining operations related to user management.
 */
public interface UserService {

    /**
     * Creates a new user based on the provided user data.
     * @param userDtoShort The user data.
     * @return The created user.
     */
    UserDto createUser(UserDtoShort userDtoShort);

    /**
     * Deletes a user with the specified ID.
     * @param userId The ID of the user to delete.
     */
    void deleteUser(int userId);

    /**
     * Retrieves a list of all users.
     * @return A list of user DTOs.
     */
    List<UserDto> getAllUsers();

    /**
     * Retrieves a user by their ID.
     * @param userId The ID of the user to retrieve.
     * @return The user DTO.
     */
    UserDto getUserById(int userId);
}