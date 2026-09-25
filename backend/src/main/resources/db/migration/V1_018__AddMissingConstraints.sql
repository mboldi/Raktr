ALTER TABLE scannables
    ALTER COLUMN category_name SET NOT NULL,
    ALTER COLUMN location_name SET NOT NULL,
    ALTER COLUMN owner_id SET NOT NULL,
    ADD CONSTRAINT scannables_device_status_not_null
        CHECK (type <> 'DEVICE' OR status IS NOT NULL);

ALTER TABLE rent_items
    ADD CONSTRAINT rent_items_rent_scannable_unique UNIQUE (rent_id, scannable_id);
