package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.User;
import com.aljamour.pkveksamen.Model.UserRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {


    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(String userName, String email, String userPassword, UserRole role) {
        jdbcTemplate.update(
                "INSERT INTO User(username, user_password, email, role) VALUES (?, ?, ?, ?)",
                userName, userPassword, email, role.name()
        );

        System.out.println(userName + email + userPassword + role);


    }


    // NY METODE: Henter en bruger baseret på ID
    public User findUserById(long userId) {
        String sql = "SELECT user_id, username, role FROM user WHERE user_id = ?";

        // Bruger queryForObject til at hente én række og mappe den til User-objektet
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setUserName(rs.getString("username"));
                user.setRole(UserRole.valueOf(rs.getString("role"))); // Antaget at din rolle er en ENUM
                return user;
            }, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int validateLogin(String userName, String userPassword) {
        String sql = "SELECT user_id FROM user WHERE username = ? AND user_password = ?";

        var result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                userName,
                userPassword
        );

        // Hvis ingen bruger findes, returner 0
        return result.isEmpty() ? 0 : result.get(0);
    }
}