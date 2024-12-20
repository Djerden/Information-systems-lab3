package com.djeno.backend_lab1.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDTO {
    private String name;
    private String eyeColor;
    private String hairColor;
    private Long locationId;
    private float weight;
    private String nationality;
}
