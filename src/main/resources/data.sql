MERGE INTO friendship_status (status, name)
    KEY (status)
    VALUES (false, 'UNCONFIRMED'),
           (true, 'CONFIRMED');

MERGE INTO mpa (mpa_id, name, description)
    KEY (mpa_id)
    VALUES (1, 'G', 'General Audiences'),
           (2, 'PG', 'Parental Guidance Suggested'),
           (3, 'PG-13', 'Parents Strongly Cautioned'),
           (4, 'R', 'Restricted'),
           (5, 'NC-17', 'Adults Only');

MERGE INTO genre (genre_id, genre_name)
    KEY (genre_id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');