UPDATE scannables SET quantity = 1 WHERE type = 'DEVICE' AND quantity IS NULL;

ALTER TABLE scannables
    ADD CONSTRAINT scannables_device_quantity_not_null
        CHECK (type <> 'DEVICE' OR quantity IS NOT NULL);
