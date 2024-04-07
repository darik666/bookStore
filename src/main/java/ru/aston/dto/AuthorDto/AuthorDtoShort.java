package ru.aston.dto.AuthorDto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class defines the properties and behavior of an author
 * short DTO object to return for client.
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthorDtoShort {
    /**
     * The name of the author.
     */
    private String authorName;

    /**
     * Constructs a new AuthorDtoShort object with the given author name.
     *
     * @param authorName The name of the author.
     */
    @JsonCreator
    public AuthorDtoShort(@JsonProperty("authorName") String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}