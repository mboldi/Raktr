CREATE TABLE rents (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type TEXT NOT NULL,
    destination TEXT NOT NULL,
    issuer_id UUID NOT NULL REFERENCES users(uuid),
    renter_name TEXT NOT NULL,
    out_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_date DATE,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID NOT NULL REFERENCES users(uuid),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by UUID NOT NULL REFERENCES users(uuid)
);

CREATE INDEX idx_rents_issuer ON rents(issuer_id);
