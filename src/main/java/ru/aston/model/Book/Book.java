package ru.aston.model.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Author.Author;
import ru.aston.model.Comment.Comment;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Book {
    private int bookId;
    private String bookTitle;
    private Author author; // Many-to-One association with Author
    private List<Comment> comments; // One-to-Many association with Comment
}
