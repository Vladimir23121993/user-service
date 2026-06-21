package com.aston.user.dao;

import com.aston.user.entity.User;
import com.aston.user.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void setUpAll() {
        System.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        System.setProperty("hibernate.connection.password", postgresContainer.getPassword());
        
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void save_ShouldPersistUser() {
        User user = new User("Иван", "ivan@test.ru", 25);

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("Иван", saved.getName());
    }

    @Test
    void save_ShouldThrowException_WhenEmailDuplicate() {
        User user1 = new User("Иван", "duplicate@test.ru", 25);
        User user2 = new User("Петр", "duplicate@test.ru", 30);
        userDao.save(user1);

        assertThrows(RuntimeException.class, () -> userDao.save(user2));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        User user = new User("Иван", "ivan@test.ru", 25);
        User saved = userDao.save(user);

        Optional<User> found = userDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getName());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userDao.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user1 = new User("Иван", "ivan@test.ru", 25);
        User user2 = new User("Петр", "petr@test.ru", 30);
        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void update_ShouldUpdateUser() {
        User user = new User("Иван", "ivan@test.ru", 25);
        User saved = userDao.save(user);
        saved.setName("Иван Петрович");
        saved.setAge(26);

        User updated = userDao.update(saved);

        assertEquals("Иван Петрович", updated.getName());
        assertEquals(26, updated.getAge());
    }

    @Test
    void deleteById_ShouldReturnTrue_WhenExists() {
        User user = new User("Иван", "ivan@test.ru", 25);
        User saved = userDao.save(user);

        boolean deleted = userDao.deleteById(saved.getId());

        assertTrue(deleted);
        assertFalse(userDao.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        boolean deleted = userDao.deleteById(999L);

        assertFalse(deleted);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = new User("Иван", "ivan@test.ru", 25);
        userDao.save(user);

        Optional<User> found = userDao.findByEmail("ivan@test.ru");

        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getName());
    }

    @Test
    void findByName_ShouldReturnUsersWithMatchingName() {
        User user1 = new User("Иван Петров", "ivan@test.ru", 25);
        User user2 = new User("Петр Иванов", "petr@test.ru", 30);
        userDao.save(user1);
        userDao.save(user2);

        List<User> found = userDao.findByName("Иван");

        assertEquals(2, found.size());
    }
}