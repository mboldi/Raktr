-- Add overbooked item but with RETURNED status: device 204 (Test Microphone) has quantity=3, requesting 4
-- Validation should ignore this item because it's already returned
INSERT INTO rent_items (rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
VALUES (100, 204, 'RETURNED', 4, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');
