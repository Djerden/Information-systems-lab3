package com.djeno.backend_lab1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "X coordinate cannot be null")
    @Column(name = "x", nullable = false)
    private Float x; //Поле не может быть null

    @NotNull(message = "Y coordinate cannot be null")
    @Column(name = "y", nullable = false)
    private Integer y; //Поле не может быть null

    @Column(name = "name")
    private String name; //Поле может быть null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
