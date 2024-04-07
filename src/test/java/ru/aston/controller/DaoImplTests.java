package ru.aston.controller;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.aston.dao.authorDao.AuthorDaoImpl;
import ru.aston.dao.bookDao.BookDaoImpl;
import ru.aston.dao.commentDao.CommentDaoImpl;
import ru.aston.dao.userDao.UserDaoImpl;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.dto.BookDto.BookDto;
import ru.aston.dto.BookDto.BookShortDto;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.dto.UserDto.UserDto;
import ru.aston.dto.UserDto.UserDtoShort;
import ru.aston.service.authorService.AuthorServiceImpl;
import ru.aston.service.bookService.BookServiceImpl;
import ru.aston.service.commentService.CommentServiceImpl;
import ru.aston.service.userService.UserServiceImpl;
import ru.aston.util.TestConnectionManager;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DaoImplTests {
    private static UserServiceImpl userServiceImpl;
    private static CommentServiceImpl commentServiceImpl;
    private static AuthorServiceImpl authorServiceImpl;
    private static BookServiceImpl bookServiceImpl;

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();
        DataSource dataSource = TestConnectionManager.getDataSource(jdbcUrl, username, password);

        TestConnectionManager.initializeDatabaseSchema(dataSource);

        userServiceImpl = new UserServiceImpl(new UserDaoImpl(dataSource));
        authorServiceImpl = new AuthorServiceImpl(new AuthorDaoImpl(dataSource));
        bookServiceImpl = new BookServiceImpl(new BookDaoImpl(dataSource));
        commentServiceImpl = new CommentServiceImpl(new CommentDaoImpl(dataSource));
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    AuthorDtoShort giveAuthor1() {
        return new AuthorDtoShort("Author 1");
    }

    AuthorDtoShort giveAuthor2() {
        return new AuthorDtoShort("Author 2");
    }

    UserDtoShort giveUser1() {
        return new UserDtoShort("John");
    }

    UserDtoShort giveUser2() {
        return new UserDtoShort("Michael");
    }

    BookShortDto giveBook1WithAuthor1() {
        BookShortDto book1 = new BookShortDto();
        book1.setBookTitle("Book 1");
        book1.setAuthorId(1);
        return book1;
    }

    BookShortDto giveBook2WithAuthor2() {
        BookShortDto book2 = new BookShortDto();
        book2.setBookTitle("Book 2");
        book2.setAuthorId(2);
        return book2;
    }

    CommentShortDto giveComment1FromUser1ToBook1() {
        CommentShortDto comment1 = new CommentShortDto();
        comment1.setText("Comment 1");
        comment1.setUserId(1);
        comment1.setBookId(1);
        return comment1;
    }

    CommentShortDto giveComment2FromUser2ToBook2() {
        CommentShortDto comment2 = new CommentShortDto();
        comment2.setText("Comment 2");
        comment2.setUserId(2);
        comment2.setBookId(2);
        return comment2;
    }

    @Test
    @Order(1)
    public void testSuccessfulCreateAuthor() {
        AuthorDtoShort authorDtoShort1 = giveAuthor1();
        AuthorDtoShort authorDtoShort2 = giveAuthor2();

        AuthorDto authorDto1 = authorServiceImpl.createAuthor(authorDtoShort1);
        AuthorDto authorDto2 = authorServiceImpl.createAuthor(authorDtoShort2);

        assertNotNull(authorDto1);
        assertEquals(authorDtoShort1.getAuthorName(), authorDto1.getAuthorName());
        assertEquals(1, authorDto1.getAuthorId());
        assertNotNull(authorDto2);
        assertEquals(authorDtoShort2.getAuthorName(), authorDto2.getAuthorName());
        assertEquals(2, authorDto2.getAuthorId());
    }

    @Test
    @Order(2)
    public void testCreateAuthorSQLException() throws SQLException {
        DataSource dataSourceMock = mock(DataSource.class);
        when(dataSourceMock.getConnection()).thenThrow(new SQLException("Test exception"));

        AuthorDaoImpl authorDaoImpl = new AuthorDaoImpl(dataSourceMock);
        AuthorServiceImpl authorServiceImpl = new AuthorServiceImpl(authorDaoImpl);

        assertThrows(RuntimeException.class, () -> {
            authorServiceImpl.createAuthor(new AuthorDtoShort());
        });
    }

    @Test
    @Order(3)
    public void testUnsuccessfulDeleteAuthor() throws SQLException {
        // Attempt to delete a non-existing user
        assertThrows(RuntimeException.class, () -> {
            authorServiceImpl.deleteAuthor(66);
        });
    }

    @Test
    @Order(4)
    public void testSuccessfulDeleteAuthor() throws SQLException {
        AuthorDtoShort authorDtoShort = new AuthorDtoShort();
        authorDtoShort.setAuthorName("John");
        AuthorDto authorDto = authorServiceImpl.createAuthor(authorDtoShort);

        authorServiceImpl.deleteAuthor(authorDto.getAuthorId());
        AuthorDto authorDto1 = authorServiceImpl.getAuthorById(authorDto.getAuthorId());

        assertNull(authorDto1);
    }

    @Test
    @Order(5)
    public void testSuccessfulCreateBook() {
        BookShortDto bookShortDto1 = giveBook1WithAuthor1();
        BookShortDto bookShortDto2 = giveBook2WithAuthor2();

        BookShortDto createdBook1 = bookServiceImpl.createBook(bookShortDto1);
        BookShortDto createdBook2 = bookServiceImpl.createBook(bookShortDto2);

        assertNotNull(createdBook1);
        assertNotNull(createdBook2);
        assertEquals(1, createdBook1.getBookId());
        assertEquals(2, createdBook2.getBookId());
        assertEquals("Book 1", createdBook1.getBookTitle());
        assertEquals("Book 2", createdBook2.getBookTitle());
    }

    @Test
    @Order(6)
    public void testSuccessfulDeleteBook() throws SQLException {
        BookShortDto bookShortDto = new BookShortDto();
        bookShortDto.setBookTitle("TestBook");
        bookShortDto.setAuthorId(1);

        BookShortDto testBook = bookServiceImpl.createBook(bookShortDto);

        bookServiceImpl.deleteBook(testBook.getBookId());

        assertThrows(RuntimeException.class, () -> {
            bookServiceImpl.getBookById(testBook.getBookId());
        });
    }

    @Test
    @Order(7)
    public void testUnsuccessfulDeleteBook() throws SQLException {
        assertThrows(RuntimeException.class, () -> {
            bookServiceImpl.deleteBook(66);
        });
    }


    @Test
    @Order(8)
    public void testSuccessfulCreateUser() {
        UserDtoShort userDtoShort1 = giveUser1();
        UserDtoShort userDtoShort2 = giveUser2();

        UserDto createdUser1 = userServiceImpl.createUser(userDtoShort1);
        UserDto createdUser2 = userServiceImpl.createUser(userDtoShort2);

        assertNotNull(createdUser1);
        assertNotNull(createdUser2);
        assertEquals(userDtoShort1.getName(), createdUser1.getUserName());
        assertEquals(userDtoShort2.getName(), createdUser2.getUserName());
    }

    @Test
    @Order(9)
    public void testCreateUserSQLException() throws SQLException {
        DataSource dataSourceMock = mock(DataSource.class);
        when(dataSourceMock.getConnection()).thenThrow(new SQLException("Test exception"));

        UserDaoImpl userDaoImpl = new UserDaoImpl(dataSourceMock);
        UserServiceImpl userServiceImpl = new UserServiceImpl(userDaoImpl);

        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.createUser(new UserDtoShort());
        });
    }

    @Order(10)
    @Test
    public void testUnsuccessfulDeleteUser() throws SQLException {
        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.deleteUser(66);
        });
    }

    @Test
    @Order(11)
    public void testSuccessfulDeleteUser() throws SQLException {
        UserDtoShort userDtoShort = new UserDtoShort();
        userDtoShort.setName("John");
        UserDto userDto = userServiceImpl.createUser(userDtoShort);

        userServiceImpl.deleteUser(userDto.getUserId());
        UserDto user = userServiceImpl.getUserById(userDto.getUserId());

        assertNull(user);
    }

    @Test
    @Order(12)
    public void testGetUserByIdExistingUserWithNoCommentsAndReviewedBooks() throws SQLException {
        UserDto retrievedUser = userServiceImpl.getUserById(1);

        // Assertions
        assertNotNull(retrievedUser);
        assertEquals(1, retrievedUser.getUserId());
        assertEquals(giveUser1().getName(), retrievedUser.getUserName());
        assertEquals(0, retrievedUser.getComments().size());
        assertEquals(0, retrievedUser.getReviewedBooks().size());
    }

    @Test
    @Order(13)
    public void testGetUserByIdNonExistingUser() throws SQLException {
        UserDto retrievedUser = userServiceImpl.getUserById(100);

        assertNull(retrievedUser);
    }

    @Test
    @Order(14)
    public void testGetUserByIdSQLException() throws SQLException {
        DataSource dataSourceMock = mock(DataSource.class);
        when(dataSourceMock.getConnection()).thenThrow(new SQLException("Test exception"));

        UserDaoImpl userDaoImpl = new UserDaoImpl(dataSourceMock);
        UserServiceImpl userServiceImpl = new UserServiceImpl(userDaoImpl);

        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.getUserById(1);
        });
    }

    @Test
    @Order(15)
    public void testSuccessfulCreateComments() {
        CommentShortDto commentShortDto1 = giveComment1FromUser1ToBook1();
        CommentShortDto commentShortDto2 = giveComment2FromUser2ToBook2();

        CommentShortDto createdComment1 = commentServiceImpl.createComment(commentShortDto1);
        CommentShortDto createdComment2 = commentServiceImpl.createComment(commentShortDto2);

        assertNotNull(createdComment1);
        assertNotNull(createdComment2);
        assertEquals(1, createdComment1.getCommentId());
        assertEquals(2, createdComment2.getCommentId());
        assertEquals(commentShortDto1.getUserId(), createdComment1.getUserId());
        assertEquals(commentShortDto2.getBookId(), createdComment2.getBookId());
    }

    @Test
    @Order(16)
    public void testCreateCommentSQLException() throws SQLException {
        DataSource dataSourceMock = mock(DataSource.class);
        when(dataSourceMock.getConnection()).thenThrow(new SQLException("Test exception"));

        CommentDaoImpl commentDaoImpl = new CommentDaoImpl(dataSourceMock);
        CommentServiceImpl commentServiceImpl = new CommentServiceImpl(commentDaoImpl);

        assertThrows(RuntimeException.class, () -> {
            commentServiceImpl.createComment(new CommentShortDto());
        });
    }

    @Test
    @Order(17)
    public void testSuccessfulDeleteComment() throws SQLException {
        CommentShortDto commentShortDto = giveComment1FromUser1ToBook1();

        CommentShortDto createdComment = commentServiceImpl.createComment(commentShortDto);

        commentServiceImpl.deleteComment(createdComment.getCommentId());
        CommentDto commentDto = commentServiceImpl.getCommentById(createdComment.getCommentId());

        assertNull(commentDto);
    }

    @Test
    @Order(18)
    public void testGetUserByIdExistingUserWithCommentsAndReviewedBooks() throws SQLException {
        // Retrieve user by ID
        UserDto retrievedUser = userServiceImpl.getUserById(1);

        String bookTitle = retrievedUser.getReviewedBooks().get(0).getBookTitle();
        String commentText = retrievedUser.getComments().get(0).getText();

        // Assertions
        assertNotNull(retrievedUser);
        assertEquals(1, retrievedUser.getUserId());
        assertEquals(giveUser1().getName(), retrievedUser.getUserName());
        assertEquals(1, retrievedUser.getComments().size());
        assertEquals(1, retrievedUser.getReviewedBooks().size());
        assertEquals(bookTitle, giveBook1WithAuthor1().getBookTitle());
        assertEquals(commentText, giveComment1FromUser1ToBook1().getText());
    }

    @Test
    @Order(19)
    public void testGetAllUsersByIdExistingUserWithCommentsAndReviewedBooks() throws SQLException {
        // Retrieve all users
        List<UserDto> usersList = userServiceImpl.getAllUsers();

        String bookTitleOfUser1 = usersList.get(0).getReviewedBooks().get(0).getBookTitle();
        String bookTitleOfUser2 = usersList.get(1).getReviewedBooks().get(0).getBookTitle();
        String commentOfUser1 = usersList.get(0).getComments().get(0).getText();
        String commentOfUser2 = usersList.get(1).getComments().get(0).getText();

        // Assertions
        assertNotNull(usersList);
        assertEquals(2, usersList.size());
        assertEquals(giveBook1WithAuthor1().getBookTitle(), bookTitleOfUser1);
        assertEquals(giveBook2WithAuthor2().getBookTitle(), bookTitleOfUser2);
        assertEquals(giveComment1FromUser1ToBook1().getText(), commentOfUser1);
        assertEquals(giveComment2FromUser2ToBook2().getText(), commentOfUser2);
    }

    @Test
    @Order(20)
    public void testGetAuthorByIdWithBook() {
        AuthorDtoShort authorDtoShort = giveAuthor1();
        BookShortDto bookShortDto = giveBook1WithAuthor1();

        AuthorDto authorDto = authorServiceImpl.getAuthorById(1);

        assertNotNull(authorDto);
        assertEquals(authorDto.getAuthorName(), authorDtoShort.getAuthorName());
        assertEquals(authorDto.getBooks().get(0).getBookTitle(), bookShortDto.getBookTitle());
    }

    @Test
    @Order(21)
    public void testGetAllAuthorsWithBook() {
        AuthorDtoShort authorDtoShort = giveAuthor2();
        BookShortDto bookShortDto = giveBook2WithAuthor2();

        List<AuthorDto> authorsList = authorServiceImpl.getAllAuthors();

        assertNotNull(authorsList);
        assertEquals(2, authorsList.size());
        assertEquals(authorsList.get(1).getAuthorName(), authorDtoShort.getAuthorName());
        assertEquals(authorsList.get(1).getBooks().get(0).getBookTitle(), bookShortDto.getBookTitle());
    }

    @Test
    @Order(22)
    public void testGetBookByIdWithAuthorAndComments() {

        BookDto bookDto = bookServiceImpl.getBookById(1);

        List<CommentDto> commentList = bookDto.getComments();

        assertNotNull(bookDto);
        assertNotNull(bookDto.getAuthor());
        assertEquals(giveAuthor1().getAuthorName(), bookDto.getAuthor().getAuthorName());
        assertNotNull(bookDto.getComments());
        assertEquals(1, commentList.size());
        assertEquals(giveComment1FromUser1ToBook1().getText(), commentList.get(0).getText());
    }

    @Test
    @Order(23)
    public void testGetAllBooksWithAuthorAndComments() {

        List<BookDto> bookList = bookServiceImpl.getAllBooks();
        List<CommentDto> commentsListOfBook2 = bookList.get(1).getComments();

        assertNotNull(bookList);
        assertEquals(2, bookList.size());
        assertEquals(giveBook2WithAuthor2().getBookTitle(), bookList.get(1).getBookTitle());
        assertEquals(giveAuthor2().getAuthorName(), bookList.get(1).getAuthor().getAuthorName());
        assertEquals(1, bookList.get(1).getComments().size());
        assertEquals(giveComment2FromUser2ToBook2().getText(), commentsListOfBook2.get(0).getText());
        assertEquals(giveComment2FromUser2ToBook2().getUserId(), commentsListOfBook2.get(0).getUser().getUserId());
    }

    @Test
    @Order(24)
    public void testGetCommentById() {

        CommentDto commentDto = commentServiceImpl.getCommentById(1);

        assertNotNull(commentDto);
        assertEquals(1, commentDto.getUser().getUserId());
        assertEquals(giveBook1WithAuthor1().getBookTitle(), commentDto.getBook().getBookTitle());
        assertEquals(giveComment1FromUser1ToBook1().getText(), commentDto.getText());
    }

    @Test
    @Order(25)
    public void testGetAllComments() {
        List<CommentDto> commentsList = commentServiceImpl.getAllComments();

        assertNotNull(commentsList);
        assertEquals(2, commentsList.size());
        assertEquals(1, commentsList.get(0).getCommentId());
        assertEquals(giveComment1FromUser1ToBook1().getText(), commentsList.get(0).getText());
        assertEquals(giveUser2().getName(), commentsList.get(1).getUser().getUserName());
        assertEquals(giveBook2WithAuthor2().getBookTitle(), commentsList.get(1).getBook().getBookTitle());
        assertEquals(giveComment2FromUser2ToBook2().getText(), commentsList.get(1).getText());
    }
}