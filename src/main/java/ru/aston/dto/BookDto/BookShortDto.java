package ru.aston.dto.BookDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.CommentDto.CommentDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookShortDto {
    private int bookId;
    private String bookTitle;
    private int authorId;

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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}
