-- Containers holding device 100: 200 = active (also holds device 102), 201 = deleted
INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (200, 'CONTAINER', 'CONTAINER-001', 'CONTAINER-BARCODE-001', 'Camera Kit', 500, true, false, 'test-category-for-device', 'test-location-for-device', 100, '2025-01-08T15:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T15:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (201, 'CONTAINER', 'CONTAINER-002', 'CONTAINER-BARCODE-002', 'Old Camera Kit', 300, false, true, 'test-category-for-device', 'test-location-for-device', 100, '2025-01-08T16:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-08T16:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO container_devices (container_id, device_id, quantity)
VALUES (200, 100, 1),
       (200, 102, 2),
       (201, 100, 1);

ALTER SEQUENCE scannables_id_seq RESTART WITH 300;
