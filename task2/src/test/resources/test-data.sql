DELETE FROM users_roles;
DELETE FROM users;
DELETE FROM roles;

INSERT INTO roles (id, name)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_MODERATOR'),
       (3, 'ROLE_ADMIN');

INSERT INTO users (id, username, password, email, account_non_locked, login_attempts)
VALUES (1, 'user', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'user@gmail.com', true, 0),
       (2, 'moderator', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'moderator@gmail.com', true,
        0),
       (3, 'admin', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'admin@gmail.com', true, 0);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);