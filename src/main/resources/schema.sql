DROP TABLE IF EXISTS reservation_seat;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS movie_session;

CREATE TABLE movie_session (
                               id BIGSERIAL PRIMARY KEY,
                               movie_title TEXT NOT NULL,
                               hall TEXT NOT NULL,
                               start_time TIMESTAMP NOT NULL,
                               price NUMERIC(10,2) NOT NULL
);

CREATE TABLE seat (
                      id BIGSERIAL PRIMARY KEY,
                      session_id BIGINT NOT NULL REFERENCES movie_session(id) ON DELETE CASCADE,
                      row_num INT NOT NULL,
                      seat_num INT NOT NULL,
                      reserved BOOLEAN NOT NULL DEFAULT FALSE,
                      UNIQUE(session_id, row_num, seat_num)
);

CREATE TABLE reservation (
                             id BIGSERIAL PRIMARY KEY,
                             session_id BIGINT NOT NULL REFERENCES movie_session(id) ON DELETE CASCADE,
                             customer_name TEXT NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reservation_seat (
                                  reservation_id BIGINT NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
                                  seat_id BIGINT NOT NULL REFERENCES seat(id) ON DELETE CASCADE,
                                  PRIMARY KEY(reservation_id, seat_id),
                                  UNIQUE(seat_id) -- одне місце не може бути в двох бронюваннях
);
