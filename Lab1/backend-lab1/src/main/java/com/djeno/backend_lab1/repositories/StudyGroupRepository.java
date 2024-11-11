package com.djeno.backend_lab1.repositories;


import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    // Найти группы по имени
    List<StudyGroup> findByName(String name);

    // Найти группы по координатам
    List<StudyGroup> findByCoordinates(Coordinates coordinates);

    // Найти группы по дате создания
    List<StudyGroup> findByCreationDate(LocalDate creationDate);

    // Найти группы, где количество студентов больше указанного значения
    List<StudyGroup> findByStudentsCountGreaterThan(long count);

    // Найти группы, где количество отчисленных студентов больше указанного значения
    List<StudyGroup> findByExpelledStudentsGreaterThan(int count);

    // Найти группы с определенной формой обучения
    List<StudyGroup> findByFormOfEducation(FormOfEducation formOfEducation);

    // Найти группы по семестру
    List<StudyGroup> findBySemesterEnum(Semester semesterEnum);

    // Найти группы по администратору группы
    List<StudyGroup> findByGroupAdmin(Person groupAdmin);

    // Найти группы, где количество студентов, подлежащих отчислению, больше указанного значения
    List<StudyGroup> findByShouldBeExpelledGreaterThan(long count);

    // Найти группы с указанным числом переведенных студентов или более
    List<StudyGroup> findByTransferredStudentsGreaterThanEqual(Long count);

    // Найти группы с количеством студентов меньше или равным заданному значению
    List<StudyGroup> findByStudentsCountLessThanEqual(long count);
}
