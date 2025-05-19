CREATE TABLE IF NOT EXISTS roles (
                                     id          BIGSERIAL PRIMARY KEY,
                                     name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
    );

INSERT INTO roles (name, description) VALUES
                                          ('admin', 'Administrator role'),
                                          ('user', 'User role'),
                                          ('moderator', 'Moderator role');
