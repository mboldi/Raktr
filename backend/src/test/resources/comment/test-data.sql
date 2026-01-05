INSERT INTO users (uuid, username, family_name, given_name, nickname, personal_id, groups)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin_user', 'Admin', 'User', 'Admin', 'ID-001', '["Admin"]'),
       ('00000000-0000-0000-0000-000000000003', 'candidate_user', 'Candidate', 'User', 'Candidate', 'ID-003', '["Stúdiós jelölt"]');

INSERT INTO comments (id, body, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Test comment 1', '2026-01-01 10:00:00', '00000000-0000-0000-0000-000000000001', '2026-01-01 10:00:00', '00000000-0000-0000-0000-000000000001'),
       (2, 'Test comment 2', '2026-01-01 11:00:00', '00000000-0000-0000-0000-000000000003', '2026-01-01 11:00:00', '00000000-0000-0000-0000-000000000003');

ALTER SEQUENCE comments_id_seq RESTART WITH 100;
