package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.repositories.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    @Autowired
    public StudyGroupService(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    public StudyGroup createStudyGroup(StudyGroup studyGroup) {
        return studyGroupRepository.save(studyGroup);
    }

    public Optional<StudyGroup> getStudyGroupById(Long id) {
        return studyGroupRepository.findById(id);
    }

    public List<StudyGroup> getAllStudyGroups(Pageable pageable) {
        return studyGroupRepository.findAll(pageable).getContent();
    }

    public StudyGroup updateStudyGroup(Long id, StudyGroup updatedStudyGroup) {
        if (studyGroupRepository.existsById(id)) {
            updatedStudyGroup.setId(id);
            return studyGroupRepository.save(updatedStudyGroup);
        }
        return null;
    }

    public void deleteStudyGroup(Long id) {
        if (studyGroupRepository.existsById(id)) {
            studyGroupRepository.deleteById(id);
        }
    }

    public StudyGroup findStudyGroupWithMinExpelledStudents() {
        return studyGroupRepository.findAll()
                .stream()
                .min((g1, g2) -> Integer.compare(g1.getExpelledStudents(), g2.getExpelledStudents()))
                .orElse(null);
    }

//    public long countByGroupAdminGreaterThan(Person admin) {
//        return studyGroupRepository.countByGroupAdminGreaterThan(admin);
//    }
//
//    public List<StudyGroup> findByNameContaining(String substring) {
//        return studyGroupRepository.findByNameContaining(substring);
//    }
//
//    public void expelAllStudents(Long groupId) {
//        studyGroupRepository.findById(groupId).ifPresent(studyGroup -> {
//            studyGroup.setExpelledStudents(studyGroup.getStudentsCount());
//            studyGroupRepository.save(studyGroup);
//        });
//    }

    public void addStudentToGroup(Long groupId) {
        studyGroupRepository.findById(groupId).ifPresent(studyGroup -> {
            studyGroup.setStudentsCount(studyGroup.getStudentsCount() + 1);
            studyGroupRepository.save(studyGroup);
        });
    }
}
