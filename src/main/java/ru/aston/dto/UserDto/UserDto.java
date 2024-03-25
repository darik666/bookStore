package ru.aston.dto.UserDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.model.Book.Book;
import ru.aston.model.Comment.Comment;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String userName;
    private List<CommentDto> comments;
    private List<BookDto> reviewedBooks;

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
