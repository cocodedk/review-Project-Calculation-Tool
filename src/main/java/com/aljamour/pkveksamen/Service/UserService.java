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


    public boolean createUser(String userName, String userPassword, String email, UserRole role) {
        try {
            userRepository.createUser(userName, userPassword, email, role);
            System.out.println("Bruger oprettet: " + userName + " " + email);
            return true;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Email allerede i brug: " + email);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public Integer validateLogin(String userName, String userPassword) {
        return userRepository.validateLogin(userName, userPassword);

    }

    public User getUserById(long userId) {
        return userRepository.findUserById(userId);
    }
}
