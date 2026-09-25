-- Device 204 (Test Microphone, quantity=3) is in container 202 (Test Case) with quantity 2
INSERT INTO container_devices (container_id, device_id, quantity)
VALUES (202, 204, 2);

-- An overlapping rent books the container, which takes 2 microphones
INSERT INTO rents (id, type, destination, issuer_id, renter_name, out_date, expected_return_date, closed, deleted, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (102, 'SIMPLE', 'Overlapping Event', '00000000-0000-0000-0000-000000000100', 'Szabó Gábor', '2025-03-03', '2025-03-07', false, false, '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO rent_items (rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
VALUES (102, 202, 'OUT', 1, '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Rent 100 asks for 2 microphones directly, but only 1 is left
INSERT INTO rent_items (rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
VALUES (100, 204, 'OUT', 2, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');
