INSERT INTO games_schema.publishers (company_name)
VALUES  ('EA Sports'),
        ('NINTENDO'),
        ('SONY'),
        ('BattleState Games');


INSERT INTO games_schema.games (title, publisher_id)
VALUES  ('Contra', 1),
        ('Tetris', 2),
        ('Tanki', 3),
        ('Sonic', 2);

INSERT INTO games_schema.users (nickname, email)
VALUES  ('Ivan', 'ivan@mail.ru'),
        ('Petr', 'petr@yandex.ru'),
        ('Marina', 'marina85@qq.com'),
        ('Valentina', 'valyusha@plyusha.net');

INSERT INTO games_schema.subscriptions (user_id, game_id)
VALUES  (1, 2),
        (1, 4),
        (2, 1),
        (3, 2);
