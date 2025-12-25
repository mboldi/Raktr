package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
