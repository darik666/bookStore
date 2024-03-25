package ru.aston.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.aston.dao.UserDao;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

@Service
public class UserService {
    private UserDao userDao;
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto createUser(UserDtoShort userDtoShort) {
        UserDto userDto = userDao.postUser(userDtoShort);
        return userDto;
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