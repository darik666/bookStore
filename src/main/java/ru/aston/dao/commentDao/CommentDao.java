package ru.aston.dao.commentDao;

import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

/**
 * Interface representing operations for interacting with comment data.
 */
public interface CommentDao {

    /**
     * Creates a new comment based on the provided CommentDtoShort object.
     *
     * @param commentShortDto The CommentShortDto object containing comment data.
     * @return The created CommentDto object.
     */
    CommentShortDto postComment(CommentShortDto commentShortDto);

    /**
     * Deletes a comment with the specified ID.
     *
     * @param commentId The ID of the comment to delete.
     */
    void deleteComment(int commentId);

    /**
     * Retrieves a comment by ID.
     *
     * @param commentId The ID of the comment to retrieve.
     * @return The CommentDto object representing the retrieved comment, or null if not found.
     */
    CommentDto getCommentById(int commentId);

    /**
     * Retrieves all comments.
     *
     * @return A list of CommentDto objects representing all comments.
     */
    List<CommentDto> getAllComments();
}