package ru.aston.dto.UserDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class defines the properties and behavior of a user DTO object.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String userName;
    private List<CommentDto> comments = new ArrayList<>();
    private List<BookDto> reviewedBooks = new ArrayList<>();

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public List<BookDto> getReviewedBooks() {
        return reviewedBooks;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public void setReviewedBooks(List<BookDto> reviewedBooks) {
        this.reviewedBooks = reviewedBooks;
    }
}