INSERT INTO app_user (username, password_hash, role_id)
VALUES
    (
        'user',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOeY5f4Y9fI8e1N7WwC4UuH9l2Y8O8c4y',
        (SELECT id FROM role WHERE name = 'USER')
    ),
    (
        'admin',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOeY5f4Y9fI8e1N7WwC4UuH9l2Y8O8c4y',
        (SELECT id FROM role WHERE name = 'ADMIN')
    );
