package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.DTO.StudyGroupHistoryResponseDTO;
import com.djeno.backend_lab1.DTO.StudyGroupResponseDTO;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.StudyGroupHistory;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import com.djeno.backend_lab1.service.data.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/study-groups")
@RequiredArgsConstructor
public class StudyGroupsContoller {

    private final StudyGroupService studyGroupService;

    // Создание новой группы
    @PostMapping
    public ResponseEntity<Void> createStudyGroup(
            @RequestBody StudyGroupDTO studyGroupDTO) {
        studyGroupService.createStudyGroup(studyGroupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Получение всех групп
    @GetMapping("/filter")
    public ResponseEntity<Page<StudyGroupResponseDTO>> filterAndSortStudyGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) FormOfEducation formOfEducation,
            @RequestParam(required = false) Semester semesterEnum,
            @RequestParam(required = false) LocalDate creationDate,
            @RequestParam(required = false) String adminName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<StudyGroupResponseDTO> studyGroups = studyGroupService.filterAndSortStudyGroups(
                name, formOfEducation, semesterEnum, creationDate, adminName, pageable);

        return ResponseEntity.ok(studyGroups);
    }

    // Получение группы по id
    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupResponseDTO> getStudyGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getStudyGroupResponseDTOById(id));
    }

    // Изменение группы по id
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStudyGroup(
            @PathVariable Long id,
            @RequestBody StudyGroupDTO studyGroupDTO) {
        studyGroupService.updateStudyGroup(id, studyGroupDTO);
        return ResponseEntity.ok().build();
    }

    // Удаление группы по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable Long id) {
        studyGroupService.deleteStudyGroup(id);
        return ResponseEntity.noContent().build();
    }

    // Получение истории группы по id
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StudyGroupHistoryResponseDTO>> getStudyGroupHistory(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getHistory(id));
    }

    // Вернуть один (любой) объект, значение поля expelledStudents которого является минимальным
    @GetMapping("/min-expelled-students")
    public ResponseEntity<StudyGroupResponseDTO> getGroupWithMinExpelledStudents() {
        StudyGroupResponseDTO studyGroup = studyGroupService.getStudyGroupWithMinExpelledStudents();
        return ResponseEntity.ok(studyGroup);
    }

    // Вернуть количество объектов, значение поля groupAdmin которых больше заданного
    @GetMapping("/count-by-admin")
    public ResponseEntity<Long> countGroupsWithAdminGreaterThan(@RequestParam Long adminId) {
        return ResponseEntity.ok(studyGroupService.countStudyGroupsWithAdminGreaterThan(adminId));
    }

    // Отчислить всех студентов указанной группы
    @PostMapping("/{id}/expel-students")
    public ResponseEntity<Void> expelAllStudents(@PathVariable Long id) {
        studyGroupService.expelAllStudents(id);
        return ResponseEntity.ok().build();
    }

    // Добавить студента в указанную группу
    @PostMapping("/{id}/add-student")
    public ResponseEntity<Void> addStudentToGroup(@PathVariable Long id) {
        studyGroupService.addStudentToGroup(id);
        return ResponseEntity.ok().build();
    }
}
