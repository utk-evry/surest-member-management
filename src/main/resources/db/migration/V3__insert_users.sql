INSERT INTO app_user (username, password_hash, role_id)
VALUES
    (
        'user',
        '$2a$10$duoX2d/N4FcqkgsuS7ufI.AHlUHpvsHst26cJCXLpebAw.H9FK2gK',
        (SELECT id FROM role WHERE name = 'USER')
    ),
    (
        'admin',
        '$2a$10$giBXBeqHEkDzaB2r1fAgq.ldTI1Gbr3dqIpTgOJ9u3wV29LQrlKLK',
        (SELECT id FROM role WHERE name = 'ADMIN')
    );
