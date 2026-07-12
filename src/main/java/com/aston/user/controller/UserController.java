package com.aston.user.controller;

import com.aston.user.dto.UserRequestDto;
import com.aston.user.dto.UserResponseDto;
import com.aston.user.model.UserModel;
import com.aston.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    // Явный конструктор (заменяет @RequiredArgsConstructor)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto dto) {
        return userService.createUser(dto);
    }

    @Operation(summary = "Найти пользователя по ID", description = "Возвращает пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {

        UserResponseDto user = userService.getUserById(id);

        UserModel model = new UserModel(user);

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel();
        model.add(selfLink);

        Link allLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        model.add(allLink);

        Link updateLink = linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update");
        model.add(updateLink);

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @GetMapping
    public CollectionModel<UserModel> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();

        List<UserModel> userModels = users.stream()
                .map(user -> {
                    UserModel model = new UserModel(user);
                    try {
                        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
                        model.add(selfLink);
                    } catch (Exception e) {
                        // ignore
                    }
                    return model;
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        Link createLink = linkTo(methodOn(UserController.class).createUser(null)).withRel("create");

        return CollectionModel.of(userModels, selfLink, createLink);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {

        UserResponseDto updated = userService.updateUser(id, dto);

        UserModel model = new UserModel(updated);

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel();
        model.add(selfLink);

        Link allLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        model.add(allLink);

        Link updateLink = linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update");
        model.add(updateLink);

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Найти пользователя по email", description = "Возвращает пользователя по email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/email")
    public ResponseEntity<UserModel> getUserByEmail(
            @Parameter(description = "Email пользователя", required = true, example = "ivan@test.ru")
            @RequestParam String email) {

        UserResponseDto user = userService.getUserByEmail(email);

        UserModel model = new UserModel(user);

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
        model.add(selfLink);

        Link allLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        model.add(allLink);

        Link emailLink = linkTo(methodOn(UserController.class).getUserByEmail(email)).withSelfRel();
        model.add(emailLink);

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Найти пользователей по имени", description = "Возвращает список пользователей, имя которых содержит указанную подстроку")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @GetMapping("/search")
    public CollectionModel<UserModel> getUsersByName(
            @Parameter(description = "Имя или его часть", required = true, example = "Иван")
            @RequestParam String name) {

        List<UserResponseDto> users = userService.getUsersByName(name);

        List<UserModel> userModels = users.stream()
                .map(user -> {
                    UserModel model = new UserModel(user);
                    try {
                        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
                        model.add(selfLink);
                    } catch (Exception e) {
                        // ignore
                    }
                    return model;
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).getUsersByName(name)).withSelfRel();

        return CollectionModel.of(userModels, selfLink);
    }
}