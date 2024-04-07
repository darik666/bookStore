package ru.aston.dao.userDao;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of UserDao interface for performing CRUD operations related to users.
 */
public class UserDaoImpl implements UserDao {
    private final DataSource dataSource;
    private final String postUserQuery = "INSERT INTO users (user_name) VALUES(?)";
    private final String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
    private final String getUserByIdQuery = "SELECT u.user_id, u.user_name, " +
            "       c.comment_id AS comment_id, c.text AS comment_text, " +
            "       b.book_id AS book_id, b.book_title AS book_title " +
            "FROM users u " +
            "LEFT JOIN comments c ON u.user_id = c.user_id " +
            "LEFT JOIN books b ON c.book_id = b.book_id " +
            "WHERE u.user_id = ?";
    private final String getAllUsersQuery = "SELECT u.user_id, u.user_name, " +
            "       c.comment_id AS comment_id, c.text AS comment_text, " +
            "       b.book_id AS book_id, b.book_title AS book_title " +
            "FROM users u " +
            "LEFT JOIN comments c ON u.user_id = c.user_id " +
            "LEFT JOIN books b ON c.book_id = b.book_id";

    /**
     * Constructs a UserDaoImpl using the default data source.
     */
    public UserDaoImpl() {
        this.dataSource = ConnectionManager.getDataSource();
    }

    /**
     * Constructs a UserDaoImpl with a specified data source for testing purposes.
     *
     * @param dataSource The data source to be used for database operations.
     */
    public UserDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new user based on the provided UserDtoShort object.
     *
     * @param userDtoShort The UserDtoShort object containing user data.
     * @return The created UserDto object.
     */
    public UserDto postUser(UserDtoShort userDtoShort) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(postUserQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userDtoShort.getName());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        UserDto user = new UserDto();
                        user.setUserName(userDtoShort.getName());
                        user.setUserId(generatedId);
                        return user;
                    } else {
                        throw new SQLException("No generated keys obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL query", e);
        }
        return null;
    }

    /**
     * Deletes a user with the specified ID.
     *
     * @param userId The ID of the user to delete.
     */
    public void deleteUser(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteUserQuery)) {
            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("User wit id = " + userId + " not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL query", e);
        }
    }

    /**
     * Retrieves all users, including associated comments and reviewed books for each user. Fetch type: eager.
     *
     * @return A list of UserDto objects representing all users, including associated comments and reviewed books.
     */
    public List<UserDto> getAllUsers() {
        Map<Integer, UserDto> userMap;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllUsersQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            userMap = new HashMap<>();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");

                if (!userMap.containsKey(userId)) {
                    UserDto user = new UserDto();
                    user.setUserId(userId);
                    user.setUserName(resultSet.getString("user_name"));
                    if (user.getComments() == null) {
                        user.setComments(new ArrayList<>());
                    }
                    if (user.getReviewedBooks() == null) {
                        user.setReviewedBooks(new ArrayList<>());
                    }
                    userMap.put(userId, user);
                }

                if (resultSet.getInt("comment_id") != 0) {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setCommentId(resultSet.getInt("comment_id"));
                    commentDto.setText(resultSet.getString("comment_text"));
                    userMap.get(userId).getComments().add(commentDto);
                }

                if (resultSet.getInt("book_id") != 0) {
                    BookDto bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));
                    userMap.get(userId).getReviewedBooks().add(bookDto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL query", e);
        }
        return new ArrayList<>(userMap.values());
    }

    /**
     * Retrieves a user by ID, including associated comments and reviewed books. Fetch type: eager.
     *
     * @param userId The ID of the user to retrieve.
     * @return The UserDto object representing the retrieved user, including associated comments and reviewed books.
     */
    public UserDto getUserById(int userId) {
        UserDto user = null;
        List<CommentDto> comments = new ArrayList<>();
        List<BookDto> reviewedBooks = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getUserByIdQuery)) {
            preparedStatement.setInt(1, userId); // Set the user ID parameter
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (user == null) {
                        user = new UserDto();
                        user.setUserId(resultSet.getInt("user_id"));
                        user.setUserName(resultSet.getString("user_name"));
                    }

                    if (resultSet.getInt("comment_id") != 0) {
                        CommentDto comment = new CommentDto();
                        comment.setCommentId(resultSet.getInt("comment_id"));
                        comment.setText(resultSet.getString("comment_text"));
                        comments.add(comment);
                        user.getComments().add(comment);
                    }

                    if (resultSet.getInt("book_id") != 0) {
                        BookDto book = new BookDto();
                        book.setBookId(resultSet.getInt("book_id"));
                        book.setBookTitle(resultSet.getString("book_title"));
                        reviewedBooks.add(book);
                        user.getReviewedBooks().add(book);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL query", e);
        }
        return user;
    }
}