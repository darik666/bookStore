package ru.aston.dao.authorDao;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorDaoImpl implements AuthorDao {
    private final DataSource dataSource;
    private final String createAuthorQuery = "INSERT INTO authors (author_name) VALUES(?)";
    private final String deleteAuthorQuery = "DELETE FROM authors WHERE author_id = ?";
    private final String getAllAuthorsQuery = "SELECT a.author_id, a.author_name, b.book_id, b.book_title " +
            "FROM authors a " +
            "LEFT JOIN books b ON a.author_id = b.author_id";
    private final String getAuthorById = "SELECT a.author_id, a.author_name, b.book_id, b.book_title " +
            "FROM authors a " +
            "LEFT JOIN books b ON a.author_id = b.author_id " +
            "WHERE a.author_id = ?";

    public AuthorDaoImpl() {
        this.dataSource = ConnectionManager.getDataSource();
    }

    public AuthorDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AuthorDto createAuthor(AuthorDtoShort authorDtoShort) {
        try (Connection connection = dataSource.getConnection();
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllAuthorsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");

                // Retrieve the AuthorDto object from the map if it exists, or create a new one
                AuthorDto authorDto = mapAuthor.computeIfAbsent(authorId, k -> new AuthorDto());

                // Set author attributes if they are not already set
                if (authorDto.getAuthorId() == 0) {
                    authorDto.setAuthorId(authorId);
                    authorDto.setAuthorName(resultSet.getString("author_name"));
                }

                // Create a new BookDto object and set its attributes
                if (resultSet.getInt("book_id") != 0) {
                    BookDto bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));

                    // Add the book to the author's list of books
                    authorDto.getBooks().add(bookDto);
                }

                // Put the authorDto object into the map
                mapAuthor.put(authorId, authorDto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Return the list of authors extracted from the map
        return new ArrayList<>(mapAuthor.values());
    }


    public AuthorDto getAuthorById(int authorId) {
        AuthorDto authorDto = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAuthorById)) {
            preparedStatement.setInt(1, authorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    authorDto = new AuthorDto();
                    authorDto.setAuthorId(authorId);
                    authorDto.setAuthorName(resultSet.getString("author_name"));

                    // Move the logic for retrieving book information inside this if block
                    do {
                        if (resultSet.getInt("book_id") != 0) {
                            BookDto bookDto = new BookDto();
                            bookDto.setBookId(resultSet.getInt("book_id"));
                            bookDto.setBookTitle(resultSet.getString("book_title"));
                            authorDto.getBooks().add(bookDto);
                        }
                    } while(resultSet.next());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorDto;
    }

    public void deleteAuthor(int authorId) {
        try (Connection connection = dataSource.getConnection();
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
