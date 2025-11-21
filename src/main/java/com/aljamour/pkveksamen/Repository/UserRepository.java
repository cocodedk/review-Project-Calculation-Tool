package com.aljamour.pkveksamen.Repository;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
}


//    private final JdbcTemplate jdbcTemplate;
//
//    public UserRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public void createUser(String userName, String email, String userPassword, String role) {
//        jdbcTemplate.update("INSERT INTO user(username, email, user_password) VALUES (?, ? , ?) ",
//                userName, email, userPassword, role);
//        System.out.println(userName + email + userPassword);
//    }
//}
