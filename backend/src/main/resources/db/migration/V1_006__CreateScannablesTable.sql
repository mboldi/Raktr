CREATE TABLE scannables (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type TEXT NOT NULL,
    asset_tag TEXT UNIQUE NOT NULL,
    barcode TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    weight INT NOT NULL,
    public_rentable BOOLEAN NOT NULL DEFAULT FALSE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    category_name TEXT REFERENCES categories(name),
    location_name TEXT REFERENCES locations(name),
    owner_id INT REFERENCES owners(id),
    -- Device-specific fields (nullable for containers)
    manufacturer TEXT,
    model TEXT,
    serial_number TEXT,
    estimated_value INT,
    status TEXT,
    quantity INT,
    acquisition_source TEXT,
    acquisition_date DATE,
    warranty_end_date DATE,
    notes TEXT,
    -- Audit fields
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID NOT NULL REFERENCES users(uuid),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by UUID NOT NULL REFERENCES users(uuid)
);

CREATE INDEX idx_scannables_type ON scannables(type);
CREATE INDEX idx_scannables_category ON scannables(category_name);
CREATE INDEX idx_scannables_location ON scannables(location_name);
CREATE INDEX idx_scannables_owner ON scannables(owner_id);
