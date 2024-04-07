package ru.aston.service.commentService;

import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

/**
 * The interface defining operations related to comment management.
 */
public interface CommentService {

    /**
     * Creates a new comment based on the provided comment data.
     * @param commentShortDto The comment data.
     * @return The created comment.
     */
    CommentShortDto createComment(CommentShortDto commentShortDto);

    /**
     * Deletes a comment with the specified ID.
     * @param commentId The ID of the comment to delete.
     */
    void deleteComment(int commentId);

    /**
     * Retrieves a list of all comments.
     * @return A list of comment DTOs.
     */
    List<CommentDto> getAllComments();

    /**
     * Retrieves a comment by their ID.
     * @param commentId The ID of the comment to retrieve.
     * @return The comment DTO.
     */
    CommentDto getCommentById(int commentId);
}