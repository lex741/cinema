package com.lex.cinema.repository.jdbctemplate;

import com.lex.cinema.model.Seat;
import com.lex.cinema.repository.SeatDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Repository
public class JdbcTemplateSeatDao implements SeatDao {

    private final JdbcTemplate jdbc;

    public JdbcTemplateSeatDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void createForSession(long sessionId, int rows, int seatsPerRow) {
        String sql = "INSERT INTO seat(session_id, row_num, seat_num, reserved) VALUES (?,?,?,false)";
        List<Object[]> batch = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                batch.add(new Object[]{sessionId, r, s});
            }
        }
        jdbc.batchUpdate(sql, batch);
    }

    @Override
    public List<Seat> findBySessionId(long sessionId) {
        String sql = "SELECT id, session_id, row_num, seat_num, reserved FROM seat WHERE session_id=? ORDER BY row_num, seat_num";
        return jdbc.query(sql, (rs, rn) -> new Seat(
                rs.getLong("id"),
                rs.getLong("session_id"),
                rs.getInt("row_num"),
                rs.getInt("seat_num"),
                rs.getBoolean("reserved")
        ), sessionId);
    }

    @Override
    public List<Seat> findByIdsForUpdate(List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return List.of();

        String in = placeholders(seatIds.size());
        String sql = "SELECT id, session_id, row_num, seat_num, reserved FROM seat WHERE id IN (" + in + ") FOR UPDATE";

        return jdbc.query(sql, (rs, rn) -> new Seat(
                rs.getLong("id"),
                rs.getLong("session_id"),
                rs.getInt("row_num"),
                rs.getInt("seat_num"),
                rs.getBoolean("reserved")
        ), seatIds.toArray());
    }

    @Override
    public int setReserved(List<Long> seatIds, boolean reserved) {
        if (seatIds == null || seatIds.isEmpty()) return 0;

        String in = placeholders(seatIds.size());
        String sql = "UPDATE seat SET reserved=? WHERE id IN (" + in + ")";
        List<Object> params = new ArrayList<>();
        params.add(reserved);
        params.addAll(seatIds);
        return jdbc.update(sql, params.toArray());
    }

    private String placeholders(int n) {
        StringJoiner j = new StringJoiner(", ");
        for (int i = 0; i < n; i++) j.add("?");
        return j.toString();
    }
}
