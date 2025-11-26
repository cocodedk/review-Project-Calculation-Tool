package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Model.User;
import com.aljamour.pkveksamen.Model.UserRole;
import com.aljamour.pkveksamen.Repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean createUser(String userName, String email, String userPassword, UserRole role) {
        try {
            userRepository.createUser(userName, email, userPassword, role);
            System.out.println("Bruger oprettet: " + userName + " " + email);
            return true;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Email allerede i brug (DataIntegrityViolation): " + email);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public int validateLogin(String userName, String userPassword) {
        int id = 0;
        id = userRepository.validateLogin(userName, userPassword);
        return id;
    }

    public User getUserById(long userId) {
        return userRepository.findUserById(userId);
    }
}
