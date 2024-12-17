package com.djeno.backend_lab1.DTO;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyGroupHistoryResponseDTO {
    private Long id;
    private Long studyGroupId;
    private String name;
    private Coordinates coordinates;
    private Person groupAdmin;
    private long studentsCount;
    private int expelledStudents;
    private Long transferredStudents;
    private FormOfEducation formOfEducation;
    private Semester semesterEnum;
    private long shouldBeExpelled;
    private int version;
    private LocalDateTime updatedAt;
    private UserDTO updatedBy; // DTO вместо полной сущности User
}
