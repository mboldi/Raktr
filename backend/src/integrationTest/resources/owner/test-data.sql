INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'test-owner-1', true, '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (2, 'test-owner-2', false, '2025-12-30T11:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-12-30T15:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE owners_id_seq RESTART WITH 100;

INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-owner', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-owner', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'TEST-DEVICE-001', '0000001', 'Test Device', 500, true, false, 'test-category-for-owner', 'test-location-for-owner', 1, 'TestManufacturer', 'TestModel', 'GOOD', 1, '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-12-30T06:00:00Z', '00000000-0000-0000-0000-000000000100');
