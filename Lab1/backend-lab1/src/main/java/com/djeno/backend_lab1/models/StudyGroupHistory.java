package com.djeno.backend_lab1.models;

import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "study_group_history")
public class StudyGroupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_group_id", nullable = false)
    private Long studyGroupId; // ID объекта, который отслеживается

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    @ManyToOne
    @JoinColumn(name = "group_admin_id")
    private Person groupAdmin;

    @Column(nullable = false)
    private long studentsCount;

    @Column(nullable = false)
    private int expelledStudents;

    @Column
    private Long transferredStudents;

    @Enumerated(EnumType.STRING)
    private FormOfEducation formOfEducation;

    @Enumerated(EnumType.STRING)
    private Semester semesterEnum;

    @Column(nullable = false)
    private long shouldBeExpelled;

    @Column(name = "version", nullable = false)
    private int version; // Номер версии

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Время изменения

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User updatedBy; // Пользователь, который обновил объект
}


