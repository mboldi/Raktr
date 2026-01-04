INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'test-owner-1', true, '2025-12-30 06:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-30 06:00:00', '00000000-0000-0000-0000-000000000100'),
       (2, 'test-owner-2', false, '2025-12-30 11:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-30 15:00:00', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE owners_id_seq RESTART WITH 100;

INSERT INTO users (uuid, username, family_name, given_name, nickname, personal_id, groups)
VALUES ('00000000-0000-0000-0000-000000000100', 'member_user_100', 'Member', 'User', 'Member', 'ID-100', '["STÚDIÓS"]');
