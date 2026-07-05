package com.aston.user.service;

import com.aston.user.dto.UserRequestDto;
import com.aston.user.dto.UserResponseDto;
import com.aston.user.entity.User;
import com.aston.user.kafka.UserEventProducer;
import com.aston.user.mapper.UserMapper;
import com.aston.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer eventProducer;

    public UserService(UserRepository userRepository, UserMapper userMapper, UserEventProducer eventProducer) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        validate(dto.getName(), dto.getEmail(), dto.getAge());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        eventProducer.sendEvent(saved.getEmail(), "CREATED");
        return userMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        validate(dto.getName(), dto.getEmail(), dto.getAge());

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setAge(dto.getAge());

        User updated = userRepository.save(existing);
        return userMapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String email = user.getEmail();
        userRepository.deleteById(id);
        eventProducer.sendEvent(email, "DELETED");
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
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