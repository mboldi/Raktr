INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-1', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100'),
       ('test-category-2', '2025-12-29T14:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T16:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-category', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-category', true, '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'DEVICE', 'TEST-DEVICE-001', '0000001', 'Test Device', 500, true, false, 'test-category-1', 'test-location-for-category', 100, 'TestManufacturer', 'TestModel', 'GOOD', 1, '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100', '2025-12-29T08:00:00', '00000000-0000-0000-0000-000000000100');
