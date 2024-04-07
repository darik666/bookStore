package ru.aston.dto.AuthorDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.dto.BookDto.BookDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class defines the properties and behavior of an author DTO object.
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthorDto {
    private int authorId;
    private String authorName;
    private List<BookDto> books = new ArrayList<>();

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<BookDto> getBooks() {
        return books;
    }
}