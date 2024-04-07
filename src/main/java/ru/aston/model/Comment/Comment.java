package ru.aston.model.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Book.Book;
import ru.aston.model.User.User;

/**
 * Represents and defines a parameters of a comment entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    /**
     * The unique ID of the comment.
     */
    private int commentId;

    /**
     * The user who made the comment.
     */
    private User user; // Many-to-One association with User

    /**
     * The book to which the comment belongs.
     */
    private Book book; // Many-to-One association with Book

    /**
     * The text content of the comment.
     */
    private String text;
}