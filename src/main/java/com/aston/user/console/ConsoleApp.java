package com.aston.user.console;

import com.aston.user.entity.User;
import com.aston.user.service.UserService;
import com.aston.user.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {

    private static final UserService service = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== User Service (Hibernate + PostgreSQL) ===\n");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Выберите действие: ");
            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> findAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> findByEmail();
                case 7 -> findByName();
                case 0 -> {
                    running = false;
                    System.out.println("До свидания!");
                }
                default -> System.out.println("Неверный пункт");
            }
        }
        HibernateUtil.shutdown();
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти по ID");
        System.out.println("3. Показать всех");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Найти по email");
        System.out.println("7. Найти по имени (часть)");
        System.out.println("0. Выход");
    }

    private static void createUser() {
        String name = readString("Имя: ");
        String email = readString("Email: ");
        int age = readInt("Возраст: ");
        try {
            User user = service.createUser(name, email, age);
            System.out.println("Создан: " + user);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void findUserById() {
        long id = readInt("ID: ");
        Optional<User> user = service.getUserById(id);
        System.out.println(user.map(Object::toString).orElse("Не найден"));
    }

    private static void findAllUsers() {
        List<User> users = service.getAllUsers();
        if (users.isEmpty()) System.out.println("Нет пользователей");
        else users.forEach(System.out::println);
    }

    private static void updateUser() {
        long id = readInt("ID пользователя для обновления: ");
        Optional<User> existing = service.getUserById(id);
        if (existing.isEmpty()) {
            System.out.println("Не найден");
            return;
        }
        String name = readString("Новое имя (оставьте пустым): ");
        String email = readString("Новый email (оставьте пустым): ");
        String ageStr = readString("Новый возраст (оставьте пустым): ");
        String finalName = name.isBlank() ? existing.get().getName() : name;
        String finalEmail = email.isBlank() ? existing.get().getEmail() : email;
        int finalAge = ageStr.isBlank() ? existing.get().getAge() : Integer.parseInt(ageStr);
        try {
            User updated = service.updateUser(id, finalName, finalEmail, finalAge);
            System.out.println("Обновлён: " + updated);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        long id = readInt("ID пользователя для удаления: ");
        boolean deleted = service.deleteUser(id);
        System.out.println(deleted ? "Удалён" : "Не найден");
    }

    private static void findByEmail() {
        String email = readString("Email: ");
        Optional<User> user = service.getUserByEmail(email);
        System.out.println(user.map(Object::toString).orElse("Не найден"));
    }

    private static void findByName() {
        String name = readString("Имя (или часть): ");
        List<User> users = service.getUsersByName(name);
        if (users.isEmpty()) System.out.println("Не найдено");
        else users.forEach(System.out::println);
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Введите число");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }
}