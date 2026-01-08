CREATE TABLE ticket_comments (
    ticket_id BIGINT NOT NULL REFERENCES tickets(id),
    comment_id BIGINT NOT NULL REFERENCES comments(id),
    PRIMARY KEY (ticket_id, comment_id)
);

CREATE INDEX idx_ticket_comments_ticket ON ticket_comments(ticket_id);
CREATE INDEX idx_ticket_comments_comment ON ticket_comments(comment_id);
