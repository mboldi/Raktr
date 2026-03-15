INSERT INTO rents (id, type, destination, issuer_id, renter_name, out_date, expected_return_date, closed, deleted, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (102, 'SIMPLE', 'Overlapping Event', '00000000-0000-0000-0000-000000000100', 'Szabó Gábor', '2025-03-01', '2025-03-10', false, false, '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO rent_items (rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
VALUES (102, 200, 'OUT', 2, '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');
