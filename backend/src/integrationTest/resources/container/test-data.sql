INSERT INTO categories (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-category-for-container', '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO locations (name, created_at, created_by, updated_at, updated_by)
VALUES ('test-location-for-container', '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100');

INSERT INTO owners (id, name, in_sch_inventory, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'test-owner-for-container', true, '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T06:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Containers: 100 = active, 101 = active with devices, 102 = deleted
INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (100, 'CONTAINER', 'CONTAINER-001', 'CONTAINER-BARCODE-001', 'Test Container', 500, true, false, 'test-category-for-container', 'test-location-for-container', 100, '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (101, 'CONTAINER', 'CONTAINER-002', 'CONTAINER-BARCODE-002', 'Container With Devices', 1000, false, false, 'test-category-for-container', 'test-location-for-container', 100, '2025-01-07T11:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T11:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (102, 'CONTAINER', 'CONTAINER-003', 'CONTAINER-BARCODE-003', 'Deleted Container', 750, false, true, 'test-category-for-container', 'test-location-for-container', 100, '2025-01-07T12:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T12:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Devices: 200, 201 = free devices to add, 202 = already in container 101
INSERT INTO scannables (id, type, asset_tag, barcode, name, weight, public_rentable, deleted, category_name, location_name, owner_id, manufacturer, model, status, quantity, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (200, 'DEVICE', 'DEVICE-001', 'DEVICE-BARCODE-001', 'Free Device 1', 100, true, false, 'test-category-for-container', 'test-location-for-container', 100, 'Manufacturer1', 'Model1', 'GOOD', 1, '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (201, 'DEVICE', 'DEVICE-002', 'DEVICE-BARCODE-002', 'Free Device 2', 150, true, false, 'test-category-for-container', 'test-location-for-container', 100, 'Manufacturer2', 'Model2', 'GOOD', 1, '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (202, 'DEVICE', 'DEVICE-003', 'DEVICE-BARCODE-003', 'Device In Container', 200, true, false, 'test-category-for-container', 'test-location-for-container', 100, 'Manufacturer3', 'Model3', 'GOOD', 1, '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T10:00:00Z', '00000000-0000-0000-0000-000000000100');

-- Link device 202 to container 101
INSERT INTO container_devices (container_id, device_id, quantity)
VALUES (101, 202, 1);

ALTER SEQUENCE scannables_id_seq RESTART WITH 300;

-- Tickets for container 100
INSERT INTO tickets (id, description, status, severity, scannable_id, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Container lid is damaged', 'OPEN', 'MAJOR', 100, '2025-01-07T14:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T14:00:00Z', '00000000-0000-0000-0000-000000000100'),
       (2, 'Label is faded', 'IN_PROGRESS', 'MINOR', 100, '2025-01-07T15:00:00Z', '00000000-0000-0000-0000-000000000100', '2025-01-07T15:00:00Z', '00000000-0000-0000-0000-000000000100');

ALTER SEQUENCE tickets_id_seq RESTART WITH 100;
