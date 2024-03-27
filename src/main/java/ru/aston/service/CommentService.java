package ru.aston.service;

import ru.aston.dao.CommentDao;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

public class CommentService {
    private final CommentDao commentDao;

    public CommentService() {
        this.commentDao = new CommentDao();
    }

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public void deleteComment(int commentId) {
        commentDao.deleteComment(commentId);
    }

    public CommentShortDto createComment(CommentShortDto commentShortDto) {
        return commentDao.postComment(commentShortDto);
    }

    public CommentDto getCommentById(int commentId) {
        return commentDao.getCommentById(commentId);
    }

    public List<CommentDto> getAllComments() {
        return commentDao.getAllComments();
    }
}
