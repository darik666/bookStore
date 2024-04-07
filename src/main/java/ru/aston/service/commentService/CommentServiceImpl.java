package ru.aston.service.commentService;

import ru.aston.dao.commentDao.CommentDaoImpl;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    private final CommentDaoImpl commentDaoImpl;

    public CommentServiceImpl() {
        this.commentDaoImpl = new CommentDaoImpl();
    }

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
