INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-ticket', '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-ticket', '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-ticket', true, '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T06:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Device for tickets
INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, acquisition_source, acquisition_date, warranty_end_date, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'DEVICE-001', 'DEVICE-BARCODE-001', 'Test Device', 500, true, false, 'test-category-for-ticket', 'test-location-for-ticket', 100, 'TestManufacturer', 'TestModel', 'GOOD', 1, 'Purchase', '2024-01-01', '2026-01-01', '2025-01-09T08:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T08:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE scannables_id_seq RESTART WITH 200;

-- Tickets with various statuses and severities for filtering tests
-- Ticket 1: OPEN, MINOR
-- Ticket 2: OPEN, MAJOR
-- Ticket 3: IN_PROGRESS, MINOR
-- Ticket 4: CLOSED, CRITICAL
INSERT INTO tickets (id, description, status, severity, scannable_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Minor issue - screen scratch', 'OPEN', 'MINOR', 100, '2025-01-09T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (2, 'Major issue - button stuck', 'OPEN', 'MAJOR', 100, '2025-01-09T11:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T11:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (3, 'Being repaired - loose cable', 'IN_PROGRESS', 'MINOR', 100, '2025-01-09T12:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T12:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (4, 'Fixed - power supply replaced', 'CLOSED', 'CRITICAL', 100, '2025-01-09T13:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-09T13:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE tickets_id_seq RESTART WITH 100;
ALTER SEQUENCE comments_id_seq RESTART WITH 100;
