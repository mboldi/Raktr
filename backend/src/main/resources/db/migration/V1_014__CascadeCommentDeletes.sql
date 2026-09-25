ALTER TABLE rent_comments
    DROP CONSTRAINT rent_comments_comment_id_fkey,
    ADD CONSTRAINT rent_comments_comment_id_fkey
        FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE;

ALTER TABLE ticket_comments
    DROP CONSTRAINT ticket_comments_comment_id_fkey,
    ADD CONSTRAINT ticket_comments_comment_id_fkey
        FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE;
