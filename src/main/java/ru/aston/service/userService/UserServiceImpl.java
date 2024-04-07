package ru.aston.service.userService;

import ru.aston.dao.userDao.UserDaoImpl;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDaoImpl;
    public UserServiceImpl() {
        this.userDaoImpl = new UserDaoImpl();
    }

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