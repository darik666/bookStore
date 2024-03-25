package ru.aston.service;

import org.springframework.stereotype.Service;
import ru.aston.dao.CommentDao;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;

import java.util.List;

@Service
public class CommentService {
    private CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public void deleteComment(int commentId) {
        commentDao.deleteComment(commentId);
    }

    public CommentShortDto postComment(CommentShortDto commentShortDto) {
        return commentDao.postComment(commentShortDto);
    }

    public CommentDto getCommentById(int commentId) {
        return commentDao.getCommentById(commentId);
    }

    public List<CommentDto> getAllComments() {
        return commentDao.getAllComments();
    }
}
