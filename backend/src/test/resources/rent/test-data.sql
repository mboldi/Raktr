INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-rent', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-rent', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (200, 'test-owner-for-rent', true, '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, serial_number, estimated_value, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (200, 'DEVICE', 'RENT-DEVICE-001', 'RENT-BARCODE-001', 'Test Camera', 1500, true, false, 'test-category-for-rent', 'test-location-for-rent', 200, 'Sony', 'A7III', 'SN-R001', 250000, 'GOOD', 5, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (201, 'DEVICE', 'RENT-DEVICE-002', 'RENT-BARCODE-002', 'Test Lens', 800, true, false, 'test-category-for-rent', 'test-location-for-rent', 200, 'Canon', 'EF 50mm', 'SN-R002', 45000, 'GOOD', 3, '2025-01-08T11:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T11:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO rents (id, type, destination, issuer_id, renter_name, out_date, expected_return_date, closed, deleted, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'SIMPLE', 'Test Event', '00000000-0000-0000-0000-000000000100', 'Nagy Péter', '2025-03-01', '2025-03-05', false, false, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO rent_items (id, rent_id, scannable_id, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 100, 200, 'OUT', 2, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (101, 100, 201, 'OUT', 1, '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T10:00:00Z', '00000000-0000-0000-0000-000000000100');
