package com.swd.services;

import com.swd.entities.User;
import com.swd.exception.EntityNotFoundException;
import com.swd.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "userId", id.toString()));
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    public Boolean existByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhone(phoneNumber);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
