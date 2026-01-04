CREATE TABLE locations (
      name TEXT PRIMARY KEY,
      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
      created_by UUID NOT NULL,
      updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
      updated_by UUID NOT NULL
);
