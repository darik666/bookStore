package ru.aston.model.Author;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Book.Book;

import java.util.List;

/**
 * Class defines the properties and behavior of a DTO author object.
 */
@Getter
@Setter
@NoArgsConstructor
public class Author {

    /**
     * Identifier for the author.
     */
    private int authorId;

    /**
     * The name of an author.
     */
    private String authorName;

    /**
     * The list of books associated with author.
     */
    private List<Book> books; // One-to-Many association with Book
}