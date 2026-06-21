package com.aston.user.service;

import com.aston.user.dao.UserDao;
import com.aston.user.dao.UserDaoImpl;
import com.aston.user.entity.User;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    // Конструктор по умолчанию (использует реальный DAO)
    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    // Конструктор для тестов (принимает мок)
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        validate(name, email, age);
        return userDao.save(new User(name, email, age));
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        validate(name, email, age);
        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }
        User user = existing.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    public boolean deleteUser(Long id) {
        return userDao.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public List<User> getUsersByName(String name) {
        return userDao.findByName(name);
    }

    private void validate(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age == null || age < 0 || age > 150) {
            throw new IllegalArgumentException("Возраст 0-150");
        }
    }
}