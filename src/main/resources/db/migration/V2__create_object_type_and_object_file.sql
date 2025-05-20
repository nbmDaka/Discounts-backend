CREATE TABLE object_type (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO object_type (name) VALUES
                                   ('CATEGORY'),
                                   ('MARKET'),
                                   ('DISCOUNT'),
                                   ('USER');

CREATE TABLE object_file (
                             id BIGSERIAL PRIMARY KEY,
                             object_type_id INT NOT NULL REFERENCES object_type(id),
                             object_id BIGINT NOT NULL,
                             image_url TEXT NOT NULL,
                             is_primary BOOLEAN NOT NULL DEFAULT TRUE,
                             is_active BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);