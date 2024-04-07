package ru.aston.service.commentService;

import ru.aston.dao.commentDao.CommentDaoImpl;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

/**
 * Implementation of interface defining operations related to comment management.
 */
public class CommentServiceImpl implements CommentService {

    /**
     * The data access object for interacting with comment data in the database.
     */
    private final CommentDaoImpl commentDaoImpl;

    /**
     * Constructs a new CommentServiceImpl instance for regular application use.
     */
    public CommentServiceImpl() {
        this.commentDaoImpl = new CommentDaoImpl();
    }

    /**
     * Constructs a new CommentServiceImpl instance for testing purposes.
     *
     * @param commentDaoImpl The CommentDaoImpl instance to be used.
     */
    public CommentServiceImpl(CommentDaoImpl commentDaoImpl) {
        this.commentDaoImpl = commentDaoImpl;
    }

    public void deleteComment(int commentId) {
        commentDaoImpl.deleteComment(commentId);
    }

    public CommentShortDto createComment(CommentShortDto commentShortDto) {
        return commentDaoImpl.postComment(commentShortDto);
    }

    public CommentDto getCommentById(int commentId) {
        return commentDaoImpl.getCommentById(commentId);
    }

    public List<CommentDto> getAllComments() {
        return commentDaoImpl.getAllComments();
    }
}