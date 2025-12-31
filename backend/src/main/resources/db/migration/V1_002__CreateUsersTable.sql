CREATE TABLE users (
      uuid UUID PRIMARY KEY,
      username TEXT UNIQUE NOT NULL,
      family_name TEXT NOT NULL,
      given_name TEXT NOT NULL,
      nickname TEXT,
      personal_id TEXT,
      roles JSONB
);
