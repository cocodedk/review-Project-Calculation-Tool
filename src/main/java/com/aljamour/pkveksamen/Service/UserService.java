package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Repository.UserRepository;
import org.apache.catalina.User;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

//    public boolean createUser(String userName, String email, String userPassword, String role) {
//        try {
//            userRepository.createUser(userName, email, userPassword, role);
//            System.out.println("Bruger oprettet: " + userName + " " + email);
//            return true;
//        } catch (DuplicateKeyException e) {
//            System.out.println("Email allerede i brug: " + email);
//            e.printStackTrace();
//            return false;
//        } catch (Exception e) {
//            System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//     public int validateLogin(String username, String userPassword) {
//        int id = 0;
//        id = userRepository.validateLogin(username, userPassword);
//        return id;
//    }
//}
