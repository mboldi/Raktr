INSERT INTO owners (id, name, in_sch_inventory, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES (1, 'test-owner-1', true, '2025-12-30 06:00:00', '2025-12-30 06:00:00'),
       (2, 'test-owner-2', false, '2025-12-30 11:00:00', '2025-12-30 15:00:00');

ALTER SEQUENCE owners_id_seq RESTART WITH 100;
