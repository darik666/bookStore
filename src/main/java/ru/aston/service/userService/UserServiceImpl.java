package ru.aston.service.userService;

import ru.aston.dao.userDao.UserDaoImpl;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

/**
 * Implementation of interface defining operations related to user management.
 */
public class UserServiceImpl implements UserService {

    /**
     * The data access object for interacting with user data in the database.
     */
    private final UserDaoImpl userDaoImpl;

    /**
     * Constructs a new UserServiceImpl instance for regular application use.
     */
    public UserServiceImpl() {
        this.userDaoImpl = new UserDaoImpl();
    }

    /**
     * Constructs a new UserServiceImpl instance for testing purposes.
     *
     * @param userDaoImpl The AuthorDaoImpl instance to be used.
     */
    public UserServiceImpl(UserDaoImpl userDaoImpl) {
        this.userDaoImpl = userDaoImpl;
    }

    public UserDto createUser(UserDtoShort userDtoShort) {
        return userDaoImpl.postUser(userDtoShort);
    }

    public void deleteUser(int userId) {
        userDaoImpl.deleteUser(userId);
    }

    public List<UserDto> getAllUsers() {
        return userDaoImpl.getAllUsers();
    }

    public UserDto getUserById(int userId) {
        return userDaoImpl.getUserById(userId);
    }
}