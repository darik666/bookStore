package ru.aston.dao;

import org.springframework.stereotype.Service;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorDao {
    private final String createAuthorQuery = "INSERT INTO authors (author_name) VALUES(?)";
    private final String deleteAuthorQuery = "DELETE FROM authors WHERE author_id = ?";
    private final String getAllAuthorsQuery = "SELECT a.author_id, a.author_name, b.book_id, b.book_title " +
            "FROM authors a " +
            "LEFT JOIN books b ON a.author_id = b.author_id";
    private final String getAuthorById = "SELECT a.author_id, a.author_name, b.book_id, b.book_title " +
            "FROM authors a " +
            "LEFT JOIN books b ON a.author_id = b.author_id " +
            "WHERE a.author_id = ?";

    public AuthorDto createAuthor(AuthorDtoShort authorDtoShort) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(createAuthorQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, authorDtoShort.getAuthorName());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        AuthorDto authorDto = new AuthorDto();
                        authorDto.setAuthorId(id);
                        authorDto.setAuthorName(authorDtoShort.getAuthorName());
                        return authorDto;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("No generated keys returned", e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting author", e);
        }
        return null;
    }

    public List<AuthorDto> getAllAuthors() {
        Map<Integer, AuthorDto> mapAuthor = new HashMap<>();

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllAuthorsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");

                // If the author is not already in the map, create a new AuthorDto object and add it to the map
                if (!mapAuthor.containsKey(authorId)) {
                    AuthorDto authorDto = new AuthorDto();
                    authorDto.setAuthorId(authorId);
                    authorDto.setAuthorName(resultSet.getString("author_name"));
                    mapAuthor.put(authorId, authorDto);
                }

                // Create a new BookDto object and set its attributes
                if (resultSet.getInt("book_id") != 0) {
                    BookDto bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));

                    // Check if the author already has a books list
                    if (mapAuthor.get(authorId).getBooks() == null) {
                        // If not, initialize the books list
                        mapAuthor.get(authorId).setBooks(new ArrayList<>());
                    }

                    // Add the book to the author's list of books
                    mapAuthor.get(authorId).getBooks().add(bookDto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Return the list of authors extracted from the map
        return new ArrayList<>(mapAuthor.values());
    }

    public AuthorDto getAuthorById(int authorId) {
        AuthorDto authorDto = null;
        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(getAuthorById)) {
            preparedStatement.setInt(1, authorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    authorDto = new AuthorDto();
                    authorDto.setAuthorId(authorId);
                    authorDto.setAuthorName(resultSet.getString("author_name"));
                }
                if (resultSet.getInt("book_id") != 0) {
                    BookDto bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));
                    authorDto.getBooks().add(bookDto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorDto;
    }

    public void deleteAuthor(int authorId) {
        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteAuthorQuery)) {
            preparedStatement.setInt(1, authorId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Author with id = " + authorId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}