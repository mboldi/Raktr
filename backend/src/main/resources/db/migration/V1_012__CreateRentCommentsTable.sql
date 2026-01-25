CREATE TABLE rent_comments (
    rent_id BIGINT NOT NULL REFERENCES rents(id),
    comment_id BIGINT NOT NULL REFERENCES comments(id),
    PRIMARY KEY (rent_id, comment_id)
);
