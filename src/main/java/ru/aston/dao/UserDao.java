package ru.aston.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.model.Author.Author;
import ru.aston.model.Book.Book;
import ru.aston.model.Comment.Comment;
import ru.aston.model.User.User;
import ru.aston.util.ConnectionManager;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDao {
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

    public UserDto getUserById(int userId) {
        UserDto user = null;
        List<CommentDto> comments = new ArrayList<>();
        List<BookDto> reviewedBooks = new ArrayList<>();

        try (Connection connection = ConnectionManager.getConnection();
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
                    }

                    if (resultSet.getInt("book_id") != 0) {
                        BookDto book = new BookDto();
                        book.setBookId(resultSet.getInt("book_id"));
                        book.setBookTitle(resultSet.getString("book_title"));
                        reviewedBooks.add(book);
                    }
                }

                user.setComments(comments);
                user.setReviewedBooks(reviewedBooks);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<UserDto> getAllUsers() {
        Map<Integer, UserDto> userMap;
        try (Connection connection = ConnectionManager.getConnection();
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
            throw new RuntimeException(e);
        }
        return new ArrayList<>(userMap.values());
    }

    public void deleteUser(int userId) {
        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteUserQuery)) {
            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("User wit id = " + userId + " not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto postUser(UserDtoShort userDtoShort) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(postUserQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userDtoShort.getUserName());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        UserDto user = new UserDto();
                        user.setUserName(userDtoShort.getUserName());
                        user.setUserId(generatedId);
                        return user;
                    } else {
                        throw new SQLException("No generated keys obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user", e);
        }
        return null;
    }



}
