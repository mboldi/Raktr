CREATE TABLE tickets (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    description TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN',
    severity TEXT NOT NULL,
    scannable_id BIGINT NOT NULL REFERENCES scannables(id),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID NOT NULL REFERENCES users(uuid),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by UUID NOT NULL REFERENCES users(uuid)
);

CREATE INDEX idx_tickets_scannable ON tickets(scannable_id);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_severity ON tickets(severity);
