package ru.aston.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.model.Book.Book;
import ru.aston.model.User.User;

@Getter
@Setter
@NoArgsConstructor
public class Comment {
    private int commentId;
    private User user; // Many-to-One association with User
    private Book book; // Many-to-One association with Book
    private String text;
}
