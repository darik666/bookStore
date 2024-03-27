package ru.aston.service;

import ru.aston.dao.UserDao;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

public class UserService {
    private final UserDao userDao;
    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto createUser(UserDtoShort userDtoShort) {
        return userDao.postUser(userDtoShort);
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }

    public UserDto getUserById(int userId) {
        return userDao.getUserById(userId);
    }
}