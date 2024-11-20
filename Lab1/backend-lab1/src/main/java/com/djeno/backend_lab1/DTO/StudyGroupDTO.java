package com.djeno.backend_lab1.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGroupDTO {

    private Long id;
    private String name;
    private Long coordinatesId;
    private long studentsCount;
    private int expelledStudents;
    private Long transferredStudents;
    private String formOfEducation;
    private long shouldBeExpelled;
    private String semesterEnum;
    private Long groupAdminId;
}