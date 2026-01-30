CREATE TABLE ticket_comments (
    ticket_id BIGINT NOT NULL REFERENCES tickets(id),
    comment_id BIGINT NOT NULL REFERENCES comments(id),
    PRIMARY KEY (ticket_id, comment_id)
);
