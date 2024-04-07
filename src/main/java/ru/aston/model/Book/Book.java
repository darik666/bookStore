package ru.aston.model.Book;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Author.Author;
import ru.aston.model.Comment.Comment;

import java.util.List;

/**
 * Represents and defines parameters of a book entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class Book {

    /**
     * The unique ID of the book.
     */
    private int bookId;

    /**
     * The title of the book.
     */
    private String bookTitle;

    /**
     * The author of the book.
     */
    private Author author; // Many-to-One association with Author

    /**
     * The comments associated with the book.
     */
    private List<Comment> comments; // One-to-Many association with Comment
}