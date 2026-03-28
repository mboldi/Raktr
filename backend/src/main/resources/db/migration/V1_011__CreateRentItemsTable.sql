CREATE TABLE rent_items (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    rent_id BIGINT NOT NULL REFERENCES rents(id),
    scannable_id BIGINT NOT NULL REFERENCES scannables(id),
    status TEXT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID NOT NULL REFERENCES users(uuid),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by UUID NOT NULL REFERENCES users(uuid)
);

CREATE INDEX idx_rent_items_rent ON rent_items(rent_id);
CREATE INDEX idx_rent_items_scannable ON rent_items(scannable_id);
