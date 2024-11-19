package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.service.data.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/study-groups")
@RequiredArgsConstructor
public class StudyGroupsContoller {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public ResponseEntity<StudyGroup> createStudyGroup(
            @RequestBody StudyGroup studyGroup,
            @RequestParam(required = false) Long coordinatesId,
            @RequestParam(required = false) Long adminId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studyGroupService.createStudyGroup(studyGroup, coordinatesId, adminId));
    }

    @GetMapping
    public ResponseEntity<Page<StudyGroup>> getAllStudyGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        // Обработка параметров сортировки
        List<Sort.Order> orders = Arrays.stream(sort)
                .map(s -> {
                    String[] parts = s.split(",");
                    return new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]);
                })
                .toList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        return ResponseEntity.ok(studyGroupService.getAllStudyGroups(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyGroup> getStudyGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getStudyGroupById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyGroup> updateStudyGroup(
            @PathVariable Long id,
            @RequestBody StudyGroup studyGroup) {
        return ResponseEntity.ok(studyGroupService.updateStudyGroup(id, studyGroup));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable Long id) {
        studyGroupService.deleteStudyGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/min-expelled-students")
    public ResponseEntity<StudyGroup> getGroupWithMinExpelledStudents() {
        return ResponseEntity.ok(studyGroupService.getStudyGroupWithMinExpelledStudents());
    }

    @GetMapping("/count-by-admin")
    public ResponseEntity<Long> countGroupsWithAdminGreaterThan(@RequestParam Long adminId) {
        return ResponseEntity.ok(studyGroupService.countStudyGroupsWithAdminGreaterThan(adminId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyGroup>> searchGroupsByName(
            @RequestParam String substring,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studyGroupService.getStudyGroupsByNameSubstring(substring, pageable));
    }

    @PostMapping("/{id}/expel-students")
    public ResponseEntity<Void> expelAllStudents(@PathVariable Long id) {
        studyGroupService.expelAllStudents(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/add-student")
    public ResponseEntity<Void> addStudentToGroup(@PathVariable Long id) {
        studyGroupService.addStudentToGroup(id);
        return ResponseEntity.ok().build();
    }
}
