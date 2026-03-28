INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-1', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100'),
       ('test-location-2', '2026-01-02T17:00:00Z', '00000000-0000-0000-0000-000000000100', '2026-01-02T21:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-location', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-location', true, '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'TEST-DEVICE-001', '0000001', 'Test Device', 500, true, false, 'test-category-for-location', 'test-location-1', 100, 'TestManufacturer', 'TestModel', 'GOOD', 1, '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100', '2026-01-02T09:00:00Z', '00000000-0000-0000-0000-000000000100');
