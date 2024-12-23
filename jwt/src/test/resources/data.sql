INSERT INTO roles (name) VALUES
                             ('ROLE_USER'),
                             ('ROLE_ADMIN');

-- Password: 100
INSERT INTO users (username, password, email) VALUES
                                                  ('user', '$2a$10$Xl0zhSVAOOV0UMD9UUCkLOZaxPm0DxbGxNVpKQme4wzYw6/YSkGWi', 'user@example.com'),
                                                  ('admin', '$2a$10$Xl0zhSVAOOV0UMD9UUCkLOZaxPm0DxbGxNVpKQme4wzYw6/YSkGWi', 'admin@example.com');

INSERT INTO users_roles (user_id, role_id) VALUES
                                               (1, 1),  -- user has ROLE_USER
                                               (2, 2);  -- admin has ROLE_ADMIN