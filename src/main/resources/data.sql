INSERT INTO movie_session(movie_title, hall, start_time, price)
VALUES
    ('Interstellar', 'Hall A', NOW() + INTERVAL '1 day', 180.00),
    ('Dune',        'Hall B', NOW() + INTERVAL '2 days', 200.00);


INSERT INTO seat(session_id, row_num, seat_num, reserved)
SELECT 1, r, s, FALSE
FROM generate_series(1,5) r, generate_series(1,8) s;


INSERT INTO seat(session_id, row_num, seat_num, reserved)
SELECT 2, r, s, FALSE
FROM generate_series(1,4) r, generate_series(1,8) s;


INSERT INTO reservation(session_id, customer_name) VALUES (1, 'Pavlo');

INSERT INTO reservation_seat(reservation_id, seat_id)
SELECT 1, id
FROM seat
WHERE session_id=1 AND row_num=1 AND seat_num IN (1,2);

UPDATE seat
SET reserved = TRUE
WHERE session_id=1 AND row_num=1 AND seat_num IN (1,2);
