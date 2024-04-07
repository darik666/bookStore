package ru.aston.service.userService;

import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDtoShort userDtoShort);

    void deleteUser(int userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(int userId);
}
