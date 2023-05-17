-- заполнение таблицы

DELETE FROM GENRE;
ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1;
INSERT INTO genre(name) VALUES
                            ('Комедия'),
                            ('Драма'),
                            ('Мультфильм'),
                            ('Триллер'),
                            ('Документальный'),
                            ('Боевик');

DELETE FROM mpa_rating;
ALTER TABLE mpa_rating ALTER COLUMN mpa_rating_id RESTART WITH 1;
INSERT INTO mpa_rating(mpa_rating_name) VALUES
                                 ('G'),
                                 ('PG'),
                                 ('PG-13'),
                                 ('R'),
                                 ('NC-17');
