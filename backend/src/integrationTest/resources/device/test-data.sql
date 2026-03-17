INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-device', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-device', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-device', true, '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Devices: 100, 101, 102 = active with different manufacturers, 103 = deleted
INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, serial_number, estimated_value, status, quantity, acquisition_source, acquisition_date, warranty_end_date, notes, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'DEVICE-001', 'DEVICE-BARCODE-001', 'Test Camera', 1500, true, false, 'test-category-for-device', 'test-location-for-device', 100, 'Sony', 'A7III', 'SN-001', 250000, 'GOOD', 1, 'Purchase', '2024-01-15', '2026-01-15', 'Main camera for studio', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (101, 'DEVICE', 'DEVICE-002', 'DEVICE-BARCODE-002', 'Test Lens', 800, true, false, 'test-category-for-device', 'test-location-for-device', 100, 'Canon', 'EF 50mm f/1.4', 'SN-002', 45000, 'GOOD', 1, 'Donation', '2023-06-20', '2025-06-20', null, '2025-01-08T11:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T11:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (102, 'DEVICE', 'DEVICE-003', 'DEVICE-BARCODE-003', 'Test Microphone', 200, false, false, 'test-category-for-device', 'test-location-for-device', 100, 'Sony', 'ECM-B1M', 'SN-003', 80000, 'GOOD', 2, 'Purchase', '2024-03-10', '2026-03-10', 'Wireless microphone set', '2025-01-08T12:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T12:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (103, 'DEVICE', 'DEVICE-004', 'DEVICE-BARCODE-004', 'Old Tripod', 2000, false, true, 'test-category-for-device', 'test-location-for-device', 100, 'Manfrotto', 'MT055XPRO3', 'SN-004', 60000, 'SCRAPPED', 1, 'Purchase', '2020-01-01', '2022-01-01', 'Broken leg, needs repair', '2025-01-08T13:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE scannables_id_seq RESTART WITH 200;

-- Tickets for device 100
INSERT INTO tickets (id, description, status, severity, scannable_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Sensor needs cleaning', 'OPEN', 'MINOR', 100, '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T14:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE tickets_id_seq RESTART WITH 100;
