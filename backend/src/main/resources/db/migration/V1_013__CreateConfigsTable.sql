CREATE TABLE configs (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL,
    data_type TEXT NOT NULL
);

INSERT INTO configs (key, value, data_type) VALUES
    ('RENT_TEAM_NAME', '', 'STRING'),
    ('RENT_TEAM_LEADER_NAME', '', 'STRING'),
    ('RENT_FIRST_SIGNER_NAME', '', 'STRING'),
    ('RENT_FIRST_SIGNER_TITLE', '', 'STRING'),
    ('RENT_SECOND_SIGNER_NAME', '', 'STRING'),
    ('RENT_SECOND_SIGNER_TITLE', '', 'STRING'),
    ('FORCE_EAN8', 'false', 'BOOLEAN');
