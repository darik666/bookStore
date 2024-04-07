package ru.aston.dto.BookDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.CommentDto.CommentDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents and defines parameters of a book DTO object.
 */
@Getter
@Setter
@NoArgsConstructor
public class BookDto {
    private int bookId;
    private String bookTitle;
    private AuthorDto author;
    private List<CommentDto> comments = new ArrayList<>();

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}