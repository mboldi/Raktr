INSERT INTO category (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-1', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T08:00:00',
        '00000000-0000-0000-0000-000000000100'),
       ('test-category-2', '2025-12-29T14:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T16:00:00',
        '00000000-0000-0000-0000-000000000100');

INSERT INTO users (uuid, username, family_name, given_name, nickname, personal_id, groups)
VALUES ('00000000-0000-0000-0000-000000000100', 'member_user_100', 'Member', 'User', 'Member', 'ID-100', '["STÚDIÓS"]');
