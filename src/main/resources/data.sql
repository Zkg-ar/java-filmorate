-- заполнение таблицы

/*
INSERT INTO genre(name) VALUES
                            ('Комедия'),
                            ('Драма'),
                            ('Триллер'),
                            ('Ужасы'),
                            ('Боевик'),
                            ('Детектив');

SELECT * FROM genre;

INSERT INTO mpa_rating(name) VALUES
                                 ('G'),
                                 ('PG'),
                                 ('PG-13'),
                                 ('R'),
                                 ('NC-17');
INSERT INTO films(name, description, release_date, duration, mpa_rating_id)
VALUES ('Бриллиантовая рука','Некое описание фильма','1969-04-28',94,1);
SELECT * FROM films;
SELECT * FROM users;
INSERT INTO users(email, login, name, birthday) VALUES ( 'kzg29@mail.ru','kzg29','Завен','1999-04-29' );
INSERT into likes(film_id, user_id) VALUES (1,1);
INSERT into film_genre(film_id, genre_id) VALUES (1,1);
*/