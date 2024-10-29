package com.example.ecm.mapper;

import com.example.ecm.dto.requests.AddCommentRequest;
import com.example.ecm.dto.responses.AddCommentResponse;
import com.example.ecm.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public Comment toComment(AddCommentRequest request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    public AddCommentResponse toAddCommentResponse(Comment comment) {
        AddCommentResponse response = new AddCommentResponse();
        response.setId(comment.getId());
        response.setAuthor(userMapper.toCreateUserResponse(comment.getAuthor()));
        response.setCreatedAt(comment.getCreatedAt());
        response.setContent(comment.getContent());
        return response;
    }
}
