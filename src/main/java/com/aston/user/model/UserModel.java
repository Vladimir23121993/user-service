package com.aston.user.model;

import com.aston.user.dto.UserResponseDto;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "users")
public class UserModel extends RepresentationModel<UserModel> {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String createdAt;

    public UserModel() {}

    public UserModel(UserResponseDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.age = dto.getAge();
        this.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : null;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}