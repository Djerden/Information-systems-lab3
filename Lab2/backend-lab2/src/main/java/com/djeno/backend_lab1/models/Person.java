package com.djeno.backend_lab1.models;

import com.djeno.backend_lab1.models.enums.Color;
import com.djeno.backend_lab1.models.enums.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull(message = "Eye color cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor; //Поле не может быть null

    @NotNull(message = "Hair color cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private Color hairColor; //Поле не может быть null

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; //Поле может быть null

    @Positive(message = "Weight must be greater than 0")
    @Column(nullable = false)
    private float weight; //Значение поля должно быть больше 0

    @NotNull(message = "Nationality cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country nationality; //Поле не может быть null

    // Владелец, создавший запись
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", location=" + (location != null ? location.getId() : null) +
                ", weight=" + weight +
                ", nationality=" + nationality +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
