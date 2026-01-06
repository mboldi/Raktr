CREATE TABLE categories (
    name TEXT PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID NOT NULL REFERENCES users(uuid),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by UUID NOT NULL REFERENCES users(uuid)
);
