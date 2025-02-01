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

    @Override
    public String toString() {
        return "StudyGroupDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinatesId=" + coordinatesId +
                ", studentsCount=" + studentsCount +
                ", expelledStudents=" + expelledStudents +
                ", transferredStudents=" + transferredStudents +
                ", formOfEducation='" + formOfEducation + '\'' +
                ", shouldBeExpelled=" + shouldBeExpelled +
                ", semesterEnum='" + semesterEnum + '\'' +
                ", groupAdminId=" + groupAdminId +
                '}';
    }
}