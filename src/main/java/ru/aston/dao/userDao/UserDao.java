package ru.aston.dao.userDao;

import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

public interface UserDao {

    UserDto postUser(UserDtoShort userDtoShort);

    void deleteUser(int userId);

    UserDto getUserById(int userId);

    List<UserDto> getAllUsers();

}
