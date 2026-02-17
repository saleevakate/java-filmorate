DELETE FROM Film_likes;
DELETE FROM Film_genres;
DELETE FROM Friends;
DELETE FROM Film;
DELETE FROM Users;
DELETE FROM Genre;
DELETE FROM Mpa;

ALTER TABLE Users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE Film ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE Genre ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE Mpa ALTER COLUMN mpa_id RESTART WITH 1;

INSERT INTO Mpa (name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

INSERT INTO Genre (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

INSERT INTO Users (email, login, name, birthday) VALUES
('user1@yandex.ru', 'user1', 'User One', '1990-01-01'),
('user2@yandex.ru', 'user2', 'User Two', '1991-02-02'),
('user3@yandex.ru', 'user3', 'User Three', '1992-03-03');

INSERT INTO Film (name, description, release_date, duration, mpa_id) VALUES
('Интерстеллар', 'Космическая одиссея о спасении человечества', '2014-11-07', 169, 3),
('Дэдпул', 'Комедийный боевик про антигероя', '2016-02-12', 108, 4),
('Матрица', 'Философский боевик о виртуальной реальности', '1999-03-31', 136, 4);

INSERT INTO Film_genres (film_id, genre_id) VALUES
(1, 2), -- интерстеллар - драма
(1, 4), -- интерстеллар - триллер
(2, 1), -- дэдпул - комедия
(2, 6), -- дэдпул - боевик
(3, 4), -- матрица - триллер
(3, 6); -- матрица - боевик

INSERT INTO Film_likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 1);