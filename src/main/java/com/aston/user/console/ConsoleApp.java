package com.aston.user.console;

import com.aston.user.dto.UserRequestDto;
import com.aston.user.dto.UserResponseDto;
import com.aston.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApp implements CommandLineRunner {

    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== User Service Console ===");
        while (true) {
            System.out.println("\n1. Создать пользователя");
            System.out.println("2. Найти по ID");
            System.out.println("3. Показать всех");
            System.out.println("4. Обновить пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("6. Найти по email");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> findAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> findUserByEmail();
                case 0 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        Integer age = scanner.nextInt();
        scanner.nextLine();

        UserRequestDto dto = new UserRequestDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setAge(age);

        try {
            UserResponseDto created = userService.createUser(dto);
            System.out.println("Создан: " + created);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void findUserById() {
        System.out.print("ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        try {
            UserResponseDto user = userService.getUserById(id);
            System.out.println("Найден: " + user);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void findAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей");
        } else {
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        System.out.print("ID для обновления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Новое имя: ");
        String name = scanner.nextLine();
        System.out.print("Новый email: ");
        String email = scanner.nextLine();
        System.out.print("Новый возраст: ");
        Integer age = scanner.nextInt();
        scanner.nextLine();

        UserRequestDto dto = new UserRequestDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setAge(age);

        try {
            UserResponseDto updated = userService.updateUser(id, dto);
            System.out.println("Обновлён: " + updated);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.print("ID для удаления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        try {
            userService.deleteUser(id);
            System.out.println("Удалён");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void findUserByEmail() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        try {
            UserResponseDto user = userService.getUserByEmail(email);
            System.out.println("Найден: " + user);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}