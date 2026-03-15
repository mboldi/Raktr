INSERT INTO rents (id, type, destination, issuer_id, renter_name, out_date, expected_return_date, closed, deleted, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'SIMPLE', 'Device Event', '00000000-0000-0000-0000-000000000100', 'Nagy Péter', '2025-03-01', '2025-03-05', false, false, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO rent_items (id, rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 100, 100, 'OUT', 1, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE rents_id_seq RESTART WITH 200;
ALTER SEQUENCE rent_items_id_seq RESTART WITH 200;
