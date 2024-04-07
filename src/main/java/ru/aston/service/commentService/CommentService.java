package ru.aston.service.commentService;

import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

public interface CommentService {

    void deleteComment(int commentId);

    CommentShortDto createComment(CommentShortDto commentShortDto);

    CommentDto getCommentById(int commentId);

    List<CommentDto> getAllComments();
}
