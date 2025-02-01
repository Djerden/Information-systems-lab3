package com.djeno.backend_lab1.repositories;


import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> {

    // Проверка, принадлежит ли объект указанному пользователю
    boolean existsByIdAndUserId(Long id, Long userId);

    // Найти группы с подстрокой в имени
    List<StudyGroup> findByNameContaining(String name, Pageable pageable);

    // Найти группу с минимальным значением expelledStudents
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.expelledStudents = (SELECT MIN(sg2.expelledStudents) FROM StudyGroup sg2)")
    List<StudyGroup> findWithMinExpelledStudents();

    // Подсчитать количество групп, у которых id groupAdmin больше указанного
    @Query("SELECT COUNT(sg) FROM StudyGroup sg WHERE sg.groupAdmin.id > :adminId")
    long countByGroupAdminGreaterThan(@Param("adminId") Long adminId);

    boolean existsByName(String name);

}
