-- Deprecated: kept for backward compatibility with existing Docker volume mounts.
-- Use `data-mysql.sql` (MySQL) or `data-h2.sql` (H2) for profile-scoped init.

-- Demo data for local/dev only (do not use in production).
INSERT INTO role (role_name, role_description) VALUES ('Developer', 'Software Developer');

-- Passwords are stored as BCrypt hashes (no plaintext passwords).
INSERT INTO employee (username, password, email, role) VALUES ('admin', '$2a$10$osm/nysXevUixOfxPegFwexZIvLMEDaQT8H/9GNm5SjC5Va2JLP8a', 'admin@test.com', 'Admin');
INSERT INTO employee (username, password, email, role) VALUES ('dev1', '$2a$10$ROALdUE8EOyYe5vvqvojAe3ndcB4Fd/mcpnd7Q.dHfgbmfFH2/7Ie', 'dev1@test.com', 'Developer');
