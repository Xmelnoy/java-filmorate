<img width="923" height="659" alt="Untitled" src="https://github.com/user-attachments/assets/0b6ad376-1ae6-4dc3-b24e-068507831feb" />

## 1. Все фильмы с рейтингом
```
SELECT f.film_id, f.name, f.release_date, m.name AS mpa_rating
FROM films f
LEFT JOIN mpa m ON f.mpa_id = m.mpa_id;
```

## 2. Количество лайков у каждого фильма
```
SELECT f.film_id, f.name, COUNT(fl.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id, f.name;
```

## 3. Топ-10 популярных фильмов
```
SELECT f.film_id, f.name, COUNT(fl.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

## 4. Информация о пользователе по логину
```
SELECT *
FROM users
WHERE login = 'ivan';
```

## 5. Список друзей пользователя
```
SELECT u.user_id, u.name
FROM users u
JOIN friendship f ON u.user_id = f.friend_id
WHERE f.user_id = 1 AND f.status = true;
```

## 6. Фильмы определённого жанра
```
SELECT f.*
FROM films f
JOIN genre_films gf ON f.film_id = gf.film_id
JOIN genre g ON gf.genre_id = g.genre_id
WHERE g.genre_name = 'Comedy';
```

## 7. Фильмы после определённой даты
```
SELECT *
FROM films
WHERE release_date > '2015-01-01';
```
Template repository for Filmorate project.
