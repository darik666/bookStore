package ru.aston.controller.commentController;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.aston.dao.CommentDao;
import ru.aston.dto.CommentDto.CommentDto;
import ru.aston.dto.CommentDto.CommentShortDto;
import ru.aston.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable int commentId) {
        return commentService.getCommentById(commentId);
    }

    @PostMapping
    public CommentShortDto postComment(@RequestBody CommentShortDto commentShortDto) {
        return commentService.postComment(commentShortDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
    }
}
