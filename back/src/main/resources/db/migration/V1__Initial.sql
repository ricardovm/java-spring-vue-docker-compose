CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    supervisor_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX users_name ON users (name);

CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX projects_name ON projects (name);

CREATE TABLE projects_users (
    project_id BIGINT NOT NULL REFERENCES projects,
    user_id BIGINT NOT NULL REFERENCES users,
    PRIMARY KEY (project_id, user_id)
);

CREATE INDEX projects_users_user_id ON projects_users (user_id);
