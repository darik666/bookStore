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

/**
 * Implementation of AuthorDao interface for performing CRUD operations related to author.
 */
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

    /**
     * Constructs a AuthorDaoImpl using the default data source.
     */
    public AuthorDaoImpl() {
        this.dataSource = ConnectionManager.getDataSource();
    }

    /**
     * Constructs a AuthorDaoImpl with a specified data source for testing purposes.
     *
     * @param dataSource The data source to be used for database operations.
     */
    public AuthorDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new author based on the provided AuthorDtoShort object.
     *
     * @param authorDtoShort The AuthorDtoShort object containing author data.
     * @return The created AuthorDto object.
     */
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

    /**
     * Deletes an author with the specified ID.
     *
     * @param authorId The ID of the author to delete.
     */
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

    /**
     * Retrieves all authors, including associated books for each author. Fetch type: eager.
     *
     * @return A list of AuthorDto objects representing all authors, including associated books.
     */
    public List<AuthorDto> getAllAuthors() {
        Map<Integer, AuthorDto> mapAuthor = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllAuthorsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");

                AuthorDto authorDto = mapAuthor.computeIfAbsent(authorId, k -> new AuthorDto());

                if (authorDto.getAuthorId() == 0) {
                    authorDto.setAuthorId(authorId);
                    authorDto.setAuthorName(resultSet.getString("author_name"));
                }

                if (resultSet.getInt("book_id") != 0) {
                    BookDto bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));

                    authorDto.getBooks().add(bookDto);
                }
                mapAuthor.put(authorId, authorDto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(mapAuthor.values());
    }

    /**
     * Retrieves an author by ID, including associated books. Fetch type: eager.
     *
     * @param authorId The ID of the author to retrieve.
     * @return The AuthorDto object representing the retrieved author, including associated books.
     */
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
}