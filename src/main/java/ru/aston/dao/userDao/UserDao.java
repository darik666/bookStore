package ru.aston.dao.userDao;

import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

/**
 * Interface representing operations for interacting with user data.
 */
public interface UserDao {

    /**
     * Creates a new user based on the provided UserDtoShort object.
     *
     * @param userDtoShort The UserDtoShort object containing user data.
     * @return The created UserDto object.
     */
    UserDto postUser(UserDtoShort userDtoShort);

    /**
     * Deletes a user with the specified ID.
     *
     * @param userId The ID of the user to delete.
     */
    void deleteUser(int userId);

    /**
     * Retrieves a user by ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The UserDto object representing the retrieved user, or null if not found.
     */
    UserDto getUserById(int userId);

    /**
     * Retrieves all users.
     *
     * @return A list of UserDto objects representing all users.
     */
    List<UserDto> getAllUsers();
}