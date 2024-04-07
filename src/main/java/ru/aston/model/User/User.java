package ru.aston.model.User;

import lombok.*;
import ru.aston.model.Book.Book;
import ru.aston.model.Comment.Comment;

import java.util.List;

/**
 * Class defines the properties and behavior of a user object.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    /**
     * Identifier for the user.
     */
    private int userId;

    /**
     * The name of the user.
     */
    private String userName;

    /**
     * The list of comments associated with the user.
     */
    private List<Comment> comments;  // One-to-Many association with Comment

    /**
     * The list of books reviewed by the user.
     */
    private List<Book> reviewedBooks;  // Many-to-Many association with Book

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}