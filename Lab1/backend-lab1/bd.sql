CREATE TABLE coordinates (
                             id SERIAL PRIMARY KEY,
                             x FLOAT NOT NULL,
                             y DOUBLE PRECISION NOT NULL
);

CREATE TABLE location (
                          id SERIAL PRIMARY KEY,
                          x FLOAT NOT NULL,
                          y INTEGER NOT NULL,
                          name VARCHAR(255)
);

CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        eye_color VARCHAR(20) NOT NULL,
                        hair_color VARCHAR(20) NOT NULL,
                        location_id INTEGER REFERENCES location(id),
                        weight FLOAT NOT NULL,
                        nationality VARCHAR(20) NOT NULL
);

CREATE TABLE "user" (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE study_group (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             coordinates_id INTEGER NOT NULL REFERENCES coordinates(id),
                             creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             students_count BIGINT NOT NULL,
                             expelled_students INTEGER NOT NULL,
                             transferred_students BIGINT,
                             form_of_education VARCHAR(50),
                             should_be_expelled BIGINT NOT NULL,
                             semester_enum VARCHAR(20),
                             group_admin_id INTEGER REFERENCES person(id),
                             user_id INTEGER NOT NULL REFERENCES "user"(id)
);
