-- Demo data for local/dev MySQL only (do not use in production).
INSERT INTO role (role_name, role_description) VALUES ('Developer', 'Software Developer');

-- Passwords are stored as BCrypt hashes.
-- To generate new hashes, use: docker run --rm -v "$(pwd)/HashPassword.java:/app/HashPassword.java" pct-build bash -c "cd /app && SECURITY_JAR=/root/.m2/repository/org/springframework/security/spring-security-crypto/6.5.7/spring-security-crypto-6.5.7.jar && COMMONS_JAR=\$(find ~/.m2/repository -name '*commons-logging*.jar' | head -1) && javac -cp \"\$SECURITY_JAR:\$COMMONS_JAR\" HashPassword.java && java -cp \"\$SECURITY_JAR:\$COMMONS_JAR:.\" HashPassword <password>"
-- Generated with: java HashPassword admin123
INSERT INTO employee (username, password, email, role) VALUES ('admin', '$2a$10$ks.HIb6phH5L/pS3G0vTRuaBDBmR707sOY9q6ciWorJU9s9VU/sj.', 'admin@test.com', 'Admin');
-- Generated with: java HashPassword dev123
INSERT INTO employee (username, password, email, role) VALUES ('dev1', '$2a$10$UhnJbOOwV0sxg3ooOrlGmeTuoz61B2FWz4Aqop3feye/KEY23a1vG', 'dev1@test.com', 'Developer');
