package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.User;
import com.aljamour.pkveksamen.Model.UserRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {


    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(String userName, String userPassword, String email, UserRole role) {
        jdbcTemplate.update(
                "INSERT INTO User(username, user_password, email, role) VALUES (?, ?, ?, ?)",
                userName, email, userPassword, role.name()
        );

        System.out.println(userName + userPassword + email + role);


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

//    public int validateLogin(String userName, String userPassword) {
//        int id = 0;
//        id = jdbcTemplate.queryForObject("SELECT user_id FROM user WHERE username = ? AND user_password = ?",
//                Integer.class, userName, userPassword);
//
//        return id;
//    }
//}

    public Integer validateLogin(String userName, String userPassword) {
        try {
            String sql = "SELECT user_id FROM user WHERE username = ? AND user_password = ?";

            List<Integer> result = jdbcTemplate.query(sql, (rs,rowNum) ->
                 Integer.parseInt(rs.getString("user_id")),
                 userName,
                 userPassword);
            if(!result.isEmpty())
                return result.get(0);
            else
                return 0;
            // queryForObject kaster en fejl, hvis data'en ikke eksisterer i databasen
            /*return jdbcTemplate.queryForObject(
                    "SELECT user_id FROM user WHERE username = ? AND user_password = ?",
                    Integer.class,
                    userName, userPassword
            );   */
        } catch (EmptyResultDataAccessException e) {
            return null; // Brugeren findes ikke
        }
    }
}
