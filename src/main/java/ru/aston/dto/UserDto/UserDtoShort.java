package ru.aston.dto.UserDto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;

import java.util.List;

@Getter
@Setter
public class UserDtoShort {
    private String userName;

    @JsonCreator
    public UserDtoShort(@JsonProperty("name")String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
