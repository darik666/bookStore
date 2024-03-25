package ru.aston.model.User;

import lombok.*;
import ru.aston.model.Book.Book;
import ru.aston.model.Comment.Comment;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class User {
    private int userId;
    private String userName;
    private List<Comment> comments; // One-to-Many association with Comment
    private List<Book> reviewedBooks; // Many-to-Many association with Book

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setReviewedBooks(List<Book> reviewedBooks) {
        this.reviewedBooks = reviewedBooks;
    }
}
