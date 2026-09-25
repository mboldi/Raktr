INSERT INTO users (uuid, username, family_name, given_name, nickname, personal_id, groups)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin_user', 'Admin', 'User', 'Admin', 'ID-001', '["Admin"]'),
       ('00000000-0000-0000-0000-000000000003', 'candidate_user', 'Candidate', 'User', 'Candidate', 'ID-003', '["Stúdiós jelölt"]');

-- Comments 1 and 2 are standalone, 3 is attached to a rent and 4 to a ticket
INSERT INTO comments (id, body, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Test comment 1', '2026-01-01T10:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T10:00:00Z', '00000000-0000-0000-0000-000000000001'),
       (2, 'Test comment 2', '2026-01-01T11:00:00Z', '00000000-0000-0000-0000-000000000003', '2026-01-01T11:00:00Z', '00000000-0000-0000-0000-000000000003'),
       (3, 'Test comment on rent', '2026-01-01T12:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T12:00:00Z', '00000000-0000-0000-0000-000000000001'),
       (4, 'Test comment on ticket', '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO rents (id, type, destination, issuer_id, renter_name, out_date, expected_return_date, closed, deleted, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'SIMPLE', 'Test Event', '00000000-0000-0000-0000-000000000001', 'Nagy Péter', '2026-03-01', '2026-03-05', false, false, '2026-01-01T12:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T12:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO rent_comments (rent_id, comment_id)
VALUES (1, 3);

INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category', '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location', '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'test-owner', true, '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T09:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, quantity, status, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'DEVICE', 'COMMENT-DEVICE-001', 'COMMENT-BARCODE-001', 'Test Device', 500, true, false, 'test-category', 'test-location', 1, 1, 'GOOD', '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO tickets (id, description, status, severity, scannable_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Test ticket', 'OPEN', 'MINOR', 1, '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001', '2026-01-01T13:00:00Z', '00000000-0000-0000-0000-000000000001');

INSERT INTO ticket_comments (ticket_id, comment_id)
VALUES (1, 4);

ALTER SEQUENCE comments_id_seq RESTART WITH 100;
ALTER SEQUENCE rents_id_seq RESTART WITH 100;
ALTER SEQUENCE scannables_id_seq RESTART WITH 100;
ALTER SEQUENCE tickets_id_seq RESTART WITH 100;
