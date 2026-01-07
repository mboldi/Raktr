INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-scannable', '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-scannable', '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-scannable', true, '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'ASSET-001', 'BARCODE-001', 'Test Scannable Device', 1500, true, false, 'test-category-for-scannable', 'test-location-for-scannable', 100, 'TestManufacturer', 'TestModel', 'GOOD', 1, '2025-01-07T10:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T10:00:00', '00000000-0000-0000-0000-000000000100'),
       (101, 'DEVICE', 'ASSET-002', 'BARCODE-002', 'Second Test Device', 2000, false, false, 'test-category-for-scannable', 'test-location-for-scannable', 100, 'AnotherManufacturer', 'AnotherModel', 'GOOD', 2, '2025-01-07T11:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T12:00:00', '00000000-0000-0000-0000-000000000100'),
       (102, 'DEVICE', 'ASSET-003', 'BARCODE-003', 'Third Test Device', 5000, false, true, 'test-category-for-scannable', 'test-location-for-scannable', 100, 'TestManufacturer', 'AnotherModel', 'SCRAPPED', 3, '2025-01-07T11:00:00', '00000000-0000-0000-0000-000000000100', '2025-01-07T12:00:00', '00000000-0000-0000-0000-000000000100');
