-- Demo data for local H2 profile only (do not use in production).
INSERT INTO role (role_name, role_description) VALUES ('Developer', 'Software Developer');

-- Passwords are plain text here; on successful login they are transparently upgraded to BCrypt hashes.
INSERT INTO employee (username, password, email, role) VALUES ('admin', 'admin123', 'admin@test.com', 'Admin');
INSERT INTO employee (username, password, email, role) VALUES ('dev1', 'dev123', 'dev1@test.com', 'Developer');
