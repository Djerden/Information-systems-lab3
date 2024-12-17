package com.djeno.backend_lab1.DTO;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudyGroupResponseDTO {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private long studentsCount;
    private int expelledStudents;
    private Long transferredStudents;
    private FormOfEducation formOfEducation;
    private long shouldBeExpelled;
    private Semester semesterEnum;
    private Person groupAdmin;
    private UserDTO user;  // DTO для пользователя вместо полной сущности
}