package com.lex.cinema.repository.jdbcclient;

import com.lex.cinema.model.Reservation;
import com.lex.cinema.repository.ReservationDao;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClientReservationDao implements ReservationDao {

    private final JdbcClient jdbc;

    public JdbcClientReservationDao(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long create(long sessionId, String customerName) {
        // PostgreSQL: RETURNING id
        return jdbc.sql("""
                INSERT INTO reservation(session_id, customer_name, created_at)
                VALUES (:sid, :name, :ts)
                RETURNING id
                """)
                .param("sid", sessionId)
                .param("name", customerName)
                .param("ts", Timestamp.valueOf(LocalDateTime.now()))
                .query(Long.class)
                .single();
    }

    @Override
    public void addSeats(long reservationId, List<Long> seatIds) {
        for (Long seatId : seatIds) {
            jdbc.sql("INSERT INTO reservation_seat(reservation_id, seat_id) VALUES (:rid, :sid)")
                    .param("rid", reservationId)
                    .param("sid", seatId)
                    .update();
        }
    }

    @Override
    public Optional<Reservation> findById(long id) {
        List<Reservation> list = jdbc.sql("""
                SELECT id, session_id, customer_name, created_at
                FROM reservation
                WHERE id = :id
                """)
                .param("id", id)
                .query((rs, rn) -> new Reservation(
                        rs.getLong("id"),
                        rs.getLong("session_id"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        null
                ))
                .list();

        if (list.isEmpty()) return Optional.empty();

        Reservation r = list.get(0);
        r.setSeatIds(findSeatIds(r.getId()));
        return Optional.of(r);
    }

    @Override
    public List<Reservation> findAll(Long sessionId, int page, int size) {
        String base = """
                SELECT id, session_id, customer_name, created_at
                FROM reservation
                """;
        String where = (sessionId == null) ? "" : " WHERE session_id = :sid ";
        String tail = " ORDER BY created_at DESC LIMIT :lim OFFSET :off";

        JdbcClient.StatementSpec spec = jdbc.sql(base + where + tail)
                .param("lim", size)
                .param("off", page * size);

        if (sessionId != null) spec = spec.param("sid", sessionId);

        List<Reservation> res = spec.query((rs, rn) -> new Reservation(
                rs.getLong("id"),
                rs.getLong("session_id"),
                rs.getString("customer_name"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                null
        )).list();

        for (Reservation r : res) {
            r.setSeatIds(findSeatIds(r.getId()));
        }
        return res;
    }

    @Override
    public long countAll(Long sessionId) {
        if (sessionId == null) {
            Long cnt = jdbc.sql("SELECT COUNT(*) FROM reservation")
                    .query(Long.class).single();
            return cnt == null ? 0 : cnt;
        }
        Long cnt = jdbc.sql("SELECT COUNT(*) FROM reservation WHERE session_id=:sid")
                .param("sid", sessionId)
                .query(Long.class).single();
        return cnt == null ? 0 : cnt;
    }

    @Override
    public boolean deleteById(long id) {
        int deleted = jdbc.sql("DELETE FROM reservation WHERE id=:id").param("id", id).update();
        return deleted == 1;
    }

    @Override
    public List<Long> findSeatIds(long reservationId) {
        return jdbc.sql("""
                SELECT seat_id
                FROM reservation_seat
                WHERE reservation_id = :rid
                ORDER BY seat_id
                """)
                .param("rid", reservationId)
                .query(Long.class)
                .list();
    }
}
