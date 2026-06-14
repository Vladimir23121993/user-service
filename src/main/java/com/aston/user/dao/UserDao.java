package com.aston.user.dao;

import com.aston.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(User user);
    boolean deleteById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
}