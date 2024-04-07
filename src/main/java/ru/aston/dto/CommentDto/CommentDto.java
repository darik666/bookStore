package ru.aston.dto.CommentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.UserDto.UserDto;

/**
 * Represents and defines a parameters of a comment DTO object.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private int commentId;
    private UserDto user;
    private BookDto book;
    private String text;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public BookDto getBook() {
        return book;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}