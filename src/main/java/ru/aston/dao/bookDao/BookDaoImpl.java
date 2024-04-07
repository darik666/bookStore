package ru.aston.dao.bookDao;

import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BookDao interface for performing CRUD operations related to books.
 */
public class BookDaoImpl implements BookDao {
    private final DataSource dataSource;
    private final String createBookQuery = "INSERT INTO books (book_title, author_id) VALUES(?, ?)";
    private final String deleteBookQuery = "DELETE FROM books WHERE book_id = ?";
    private final String getBookByIdQuery = "SELECT b.book_id, b.book_title, a.author_id, a.author_name, " +
            "c.comment_id, c.user_id, c.text " +
            "FROM books b " +
            "LEFT JOIN authors a ON b.author_id = a.author_id " +
            "LEFT JOIN comments c ON b.book_id = c.book_id " +
            "WHERE b.book_id = ?";

    private final String getAllBooksQuery =  "SELECT b.book_id, b.book_title, a.author_id, a.author_name, " +
            "c.comment_id, c.user_id, c.text " +
            "FROM books b " +
            "LEFT JOIN authors a ON b.author_id = a.author_id " +
            "LEFT JOIN comments c ON b.book_id = c.book_id ";

    /**
     * Constructs a BookDaoImpl using the default data source.
     */
    public BookDaoImpl() {
        this.dataSource = ConnectionManager.getDataSource();
    }

    /**
     * Constructs a BookDaoImpl with a specified data source for testing purposes.
     *
     * @param dataSource The data source to be used for database operations.
     */
    public BookDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new book based on the provided BookDtoShort object.
     *
     * @param bookShortDto The BookShortDto object containing book data.
     * @return The created BookDto object.
     */
    public BookShortDto createBook(BookShortDto bookShortDto) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(createBookQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, bookShortDto.getBookTitle());
            preparedStatement.setInt(2, bookShortDto.getAuthorId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        bookShortDto.setBookId(id);
                        return bookShortDto;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("No generated keys returned", e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Deletes a book with the specified ID.
     *
     * @param bookId The ID of the book to delete.
     */
    public void deleteBook(int bookId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteBookQuery)) {
            preparedStatement.setInt(1, bookId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Book with id = " + bookId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all books, including associated comments for each book. Fetch type: eager.
     *
     * @return A list of BookDto objects representing all books, including associated comments.
     */
    public List<BookDto> getAllBooks() {
        List<BookDto> books = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllBooksQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int currentBookId = resultSet.getInt("book_id");
                BookDto bookDto = null;

                for (BookDto book : books) {
                    if (book.getBookId() == currentBookId) {
                        bookDto = book;
                        break;
                    }
                }

                if (bookDto == null) {
                    bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));

                    int authorId = resultSet.getInt("author_id");
                    if (authorId != 0) {
                        AuthorDto authorDto = new AuthorDto();
                        authorDto.setAuthorId(authorId);
                        authorDto.setAuthorName(resultSet.getString("author_name"));
                        bookDto.setAuthor(authorDto);
                    }

                    bookDto.setComments(new ArrayList<>());
                    books.add(bookDto);
                }

                int commentId = resultSet.getInt("comment_id");
                if (commentId != 0) {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setCommentId(commentId);
                    commentDto.setText(resultSet.getString("text"));

                    UserDto userDto = new UserDto();
                    userDto.setUserId(resultSet.getInt("user_id"));
                    commentDto.setUser(userDto);

                    bookDto.getComments().add(commentDto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching books", e);
        }
        return books;
    }

    /**
     * Retrieves a book by ID, including associated comments. Fetch type: eager.
     *
     * @param bookId The ID of the book to retrieve.
     * @return The BookDto object representing the retrieved book, including associated comments.
     */
    public BookDto getBookById(int bookId) {
        BookDto bookDto = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getBookByIdQuery)) {
            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (bookDto == null) {
                    bookDto = new BookDto();
                    bookDto.setBookId(resultSet.getInt("book_id"));
                    bookDto.setBookTitle(resultSet.getString("book_title"));

                    int authorId = resultSet.getInt("author_id");
                    if (authorId != 0) {
                        AuthorDto authorDto = new AuthorDto();
                        authorDto.setAuthorId(authorId);
                        authorDto.setAuthorName(resultSet.getString("author_name"));
                        bookDto.setAuthor(authorDto);
                    }

                    bookDto.setComments(new ArrayList<>());
                }

                int commentId = resultSet.getInt("comment_id");
                if (commentId != 0) {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setCommentId(commentId);
                    commentDto.setText(resultSet.getString("text"));

                    UserDto userDto = new UserDto();
                    userDto.setUserId(resultSet.getInt("user_id"));
                    commentDto.setUser(userDto);

                    bookDto.getComments().add(commentDto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving book with ID: " + bookId, e);
        }
        if (bookDto == null) {
            throw new RuntimeException("Book with ID " + bookId + " not found");
        }
        return bookDto;
    }
}