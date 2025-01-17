package ru.aston.dao.commentDao;

import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CommentDao interface for performing CRUD operations related to comments.
 */
public class CommentDaoImpl implements CommentDao {
    private final DataSource dataSource;
    private final String getAllCommentsQuery = "SELECT c.comment_id, c.user_id, c.book_id, " +
            "c.text, u.user_name, b.book_title " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.user_id " +
            "LEFT JOIN books b ON c.book_id = b.book_id ";
    private final String getCommentByIdQuery = "SELECT c.comment_id, c.user_id, c.book_id, " +
            "c.text, u.user_name, b.book_title " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.user_id " +
            "LEFT JOIN books b ON c.book_id = b.book_id " +
            "WHERE c.comment_id = ?";

    private final String postCommentQuery = "INSERT INTO comments (user_id, book_id, text) VALUES (?, ?, ?) ";
    private final String deleteCommentQuery = "DELETE FROM comments " +
            "WHERE comment_id = ? ";

    /**
     * Constructs a CommentDaoImpl using the default data source.
     */
    public CommentDaoImpl() {
        this.dataSource = ConnectionManager.getDataSource();
    }

    /**
     * Constructs a UserDaoImpl with a specified data source for testing purposes.
     *
     * @param dataSource The data source to be used for database operations.
     */
    public CommentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new comment based on the provided CommentShortDto object.
     *
     * @param commentShortDto The CommentShortDto object containing comment data.
     * @return The created CommentDto object.
     */
    public CommentShortDto postComment(CommentShortDto commentShortDto) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(postCommentQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, commentShortDto.getUserId());
            preparedStatement.setInt(2, commentShortDto.getBookId());
            preparedStatement.setString(3, commentShortDto.getText());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int commentId = generatedKeys.getInt(1);
                        commentShortDto.setCommentId(commentId);
                        return commentShortDto;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error posting comment", e);
        }
        return null;
    }

    /**
     * Deletes a comment with the specified ID.
     *
     * @param commentId The ID of the comment to delete.
     */
    public void deleteComment(int commentId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteCommentQuery)) {
            preparedStatement.setInt(1, commentId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Comment with id = " + commentId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment with id = " + commentId, e);
        }
    }

    /**
     * Retrieves all comments, including associated user and book for each comment.
     *
     * @return A list of CommentDto objects representing all comments, including associated user and book.
     */
    public List<CommentDto> getAllComments() {
        List<CommentDto> comments = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllCommentsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CommentDto comment = new CommentDto();
                comment.setCommentId(resultSet.getInt("comment_id"));
                comment.setText(resultSet.getString("text"));

                UserDto user = new UserDto();
                user.setUserId(resultSet.getInt("user_id"));
                user.setUserName(resultSet.getString("user_name"));
                comment.setUser(user);

                BookDto book = new BookDto();
                book.setBookId(resultSet.getInt("book_id"));
                book.setBookTitle(resultSet.getString("book_title"));
                comment.setBook(book);

                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching comments", e);
        }
        return comments;
    }

    /**
     * Retrieves a comment by ID, including associated user and book.
     *
     * @param commentId The ID of the comment to retrieve.
     * @return The CommentDto object representing the retrieved comment, including associated user and book.
     */
    public CommentDto getCommentById(int commentId) {
        CommentDto comment = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getCommentByIdQuery)) {
            preparedStatement.setInt(1, commentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    comment = new CommentDto();
                    comment.setCommentId(resultSet.getInt("comment_id"));
                    comment.setText(resultSet.getString("text"));

                    UserDto user = new UserDto();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setUserName(resultSet.getString("user_name"));
                    comment.setUser(user);

                    BookDto book = new BookDto();
                    book.setBookId(resultSet.getInt("book_id"));
                    book.setBookTitle(resultSet.getString("book_title"));
                    comment.setBook(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching comment with id = " + commentId, e);
        }
        return comment;
    }
}