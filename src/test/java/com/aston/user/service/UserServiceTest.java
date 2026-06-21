package com.aston.user.service;

import com.aston.user.dao.UserDao;
import com.aston.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Тестовый Иван", "ivan@test.ru", 25);
        testUser.setId(1L);
    }

    // ===== createUser =====

    @Test
    void createUser_ShouldReturnSavedUser_WhenValidData() {
        when(userDao.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("Тестовый Иван", "ivan@test.ru", 25);

        assertNotNull(result);
        assertEquals("Тестовый Иван", result.getName());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenNameIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUser("", "ivan@test.ru", 25));
        
        assertEquals("Имя не может быть пустым", exception.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUser("Иван", "neemail", 25));
        
        assertEquals("Некорректный email", exception.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenAgeIsInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUser("Иван", "ivan@test.ru", 200));
        
        assertEquals("Возраст 0-150", exception.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    // ===== getUserById =====

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Тестовый Иван", result.get().getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenNotExists() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userDao, times(1)).findById(999L);
    }

    // ===== getAllUsers =====

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userDao, times(1)).findAll();
    }

    // ===== updateUser =====

    @Test
    void updateUser_ShouldUpdateAndReturnUser_WhenValidData() {
        User updatedUser = new User("Иван Новый", "new@test.ru", 30);
        updatedUser.setId(1L);
        
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, "Иван Новый", "new@test.ru", 30);

        assertNotNull(result);
        assertEquals("Иван Новый", result.getName());
        verify(userDao, times(1)).findById(1L);
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
            () -> userService.updateUser(999L, "Иван", "ivan@test.ru", 25));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userDao, never()).update(any(User.class));
    }

    // ===== deleteUser =====

    @Test
    void deleteUser_ShouldReturnTrue_WhenUserExists() {
        when(userDao.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldReturnFalse_WhenUserNotExists() {
        when(userDao.deleteById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userDao, times(1)).deleteById(999L);
    }

    // ===== getUserByEmail =====

    @Test
    void getUserByEmail_ShouldReturnUser_WhenExists() {
        when(userDao.findByEmail("ivan@test.ru")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("ivan@test.ru");

        assertTrue(result.isPresent());
        verify(userDao, times(1)).findByEmail("ivan@test.ru");
    }

    @Test
    void getUserByEmail_ShouldReturnEmpty_WhenNotExists() {
        when(userDao.findByEmail("notexist@test.ru")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("notexist@test.ru");

        assertFalse(result.isPresent());
        verify(userDao, times(1)).findByEmail("notexist@test.ru");
    }

    // ===== getUsersByName =====

    @Test
    void getUsersByName_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userDao.findByName("Тестовый")).thenReturn(users);

        List<User> result = userService.getUsersByName("Тестовый");

        assertEquals(1, result.size());
        verify(userDao, times(1)).findByName("Тестовый");
    }
}