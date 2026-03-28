-- Add device 204 (Test Microphone, quantity=3) into container 202 (Test Case) with quantity 2
INSERT INTO container_devices (container_id, device_id, quantity)
VALUES (202, 204, 2);

-- Rent the container twice — total demand for device 204 = 2 * 2 = 4, available = 3
INSERT INTO rent_items (rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
VALUES (100, 202, 'OUT', 2, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');
