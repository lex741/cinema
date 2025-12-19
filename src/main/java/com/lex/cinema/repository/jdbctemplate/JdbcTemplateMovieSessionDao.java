package com.lex.cinema.repository.jdbctemplate;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.repository.MovieSessionDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JdbcTemplateMovieSessionDao implements MovieSessionDao {

    private final JdbcTemplate jdbc;

    public JdbcTemplateMovieSessionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long create(MovieSession s) {
        String sql = "INSERT INTO movie_session(movie_title, hall, start_time, price) VALUES (?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, s.getMovieTitle());
            ps.setString(2, s.getHall());
            ps.setTimestamp(3, Timestamp.valueOf(s.getStartTime()));
            ps.setBigDecimal(4, s.getPrice());
            return ps;
        }, kh);

        Number key = kh.getKey();
        return key == null ? 0L : key.longValue();
    }

    @Override
    public Optional<MovieSession> findById(long id) {
        String sql = "SELECT id, movie_title, hall, start_time, price FROM movie_session WHERE id=?";
        List<MovieSession> list = jdbc.query(sql, (rs, rn) -> new MovieSession(
                rs.getLong("id"),
                rs.getString("movie_title"),
                rs.getString("hall"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getBigDecimal("price")
        ), id);
        return list.stream().findFirst();
    }

    @Override
    public List<MovieSession> findAll(String title, String hall, LocalDateTime from, LocalDateTime to, int page, int size) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, movie_title, hall, start_time, price FROM movie_session WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(movie_title) LIKE ?");
            params.add("%" + title.toLowerCase() + "%");
        }
        if (hall != null && !hall.isBlank()) {
            sql.append(" AND hall = ?");
            params.add(hall);
        }
        if (from != null) {
            sql.append(" AND start_time >= ?");
            params.add(Timestamp.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND start_time <= ?");
            params.add(Timestamp.valueOf(to));
        }

        sql.append(" ORDER BY start_time ASC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        return jdbc.query(sql.toString(), (rs, rn) -> new MovieSession(
                rs.getLong("id"),
                rs.getString("movie_title"),
                rs.getString("hall"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getBigDecimal("price")
        ), params.toArray());
    }

    @Override
    public long countAll(String title, String hall, LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM movie_session WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(movie_title) LIKE ?");
            params.add("%" + title.toLowerCase() + "%");
        }
        if (hall != null && !hall.isBlank()) {
            sql.append(" AND hall = ?");
            params.add(hall);
        }
        if (from != null) {
            sql.append(" AND start_time >= ?");
            params.add(Timestamp.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND start_time <= ?");
            params.add(Timestamp.valueOf(to));
        }

        Long cnt = jdbc.queryForObject(sql.toString(), Long.class, params.toArray());
        return cnt == null ? 0 : cnt;
    }

    @Override
    public boolean update(MovieSession s) {
        String sql = "UPDATE movie_session SET movie_title=?, hall=?, start_time=?, price=? WHERE id=?";
        int updated = jdbc.update(sql,
                s.getMovieTitle(),
                s.getHall(),
                Timestamp.valueOf(s.getStartTime()),
                s.getPrice(),
                s.getId()
        );
        return updated == 1;
    }

    @Override
    public boolean deleteById(long id) {
        int deleted = jdbc.update("DELETE FROM movie_session WHERE id=?", id);
        return deleted == 1;
    }
}
