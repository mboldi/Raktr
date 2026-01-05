package hu.bsstudio.raktr.comment.service;

import hu.bsstudio.raktr.dal.repository.CommentRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final SecurityService securityService;

    @Transactional
    public void deleteComment(Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(ObjectNotFoundException::new);

        securityService.checkIsOwnerOrAdmin(comment.getCreatedBy().getUuid());

        commentRepository.delete(comment);

        log.info("Deleted Comment with ID [{}]", commentId);
    }

}
