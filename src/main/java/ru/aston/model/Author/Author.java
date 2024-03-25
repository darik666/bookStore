package ru.aston.model.Author;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Book.Book;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Author {
    private int authorId;
    private String authorName;
    private List<Book> books; // One-to-Many association with Book
}
