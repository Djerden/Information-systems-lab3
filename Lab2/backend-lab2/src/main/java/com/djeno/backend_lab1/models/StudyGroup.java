package com.djeno.backend_lab1.models;

import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull(message = "Coordinates cannot be null")
    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @NotNull(message = "Creation date cannot be null")
    @Column(name = "creation_date", nullable = false, updatable = false)
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Positive(message = "Students count must be greater than 0")
    @Column(name = "students_count", nullable = false)
    private long studentsCount; //Значение поля должно быть больше 0

    @Positive(message = "Expelled students count must be greater than 0")
    @Column(name = "expelled_students", nullable = false)
    private int expelledStudents; //Значение поля должно быть больше 0

    @PositiveOrZero(message = "Transferred students can be greater than 0 or null")
    @Column(name = "transferred_students")
    private Long transferredStudents; //Значение поля должно быть больше 0, Поле может быть null

    @Enumerated(EnumType.STRING)
    @Column(name = "form_of_education")
    private FormOfEducation formOfEducation; //Поле может быть null

    @Positive(message = "Should be expelled count must be greater than 0")
    @Column(name = "should_be_expelled", nullable = false)
    private long shouldBeExpelled; //Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @Column(name = "semester_enum")
    private Semester semesterEnum; //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "group_admin_id")
    private Person groupAdmin; //Поле может быть null

    // Владелец, создавший запись
    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + (coordinates != null ? coordinates.getId() : null) +
                ", creationDate=" + creationDate +
                ", studentsCount=" + studentsCount +
                ", expelledStudents=" + expelledStudents +
                ", transferredStudents=" + transferredStudents +
                ", formOfEducation=" + formOfEducation +
                ", shouldBeExpelled=" + shouldBeExpelled +
                ", semesterEnum=" + semesterEnum +
                ", groupAdmin=" + (groupAdmin != null ? groupAdmin.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
