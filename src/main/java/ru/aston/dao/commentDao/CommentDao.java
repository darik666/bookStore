package ru.aston.dao.commentDao;

import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

public interface CommentDao {

    List<CommentDto> getAllComments();

    CommentDto getCommentById(int commentId);

    CommentShortDto postComment(CommentShortDto commentShortDto);

    void deleteComment(int commentId);
}
