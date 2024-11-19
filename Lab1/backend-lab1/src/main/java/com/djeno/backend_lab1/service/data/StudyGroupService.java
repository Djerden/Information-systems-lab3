package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.CoordinatesRepository;
import com.djeno.backend_lab1.repositories.PersonRepository;
import com.djeno.backend_lab1.repositories.StudyGroupRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final PersonRepository personRepository;
    private final UserService userService;

    // Создание группы
    public StudyGroup createStudyGroup(StudyGroup studyGroup, Long coordinatesId, Long adminId) {
        var currentUser = userService.getCurrentUser();
        studyGroup.setUser(currentUser);

        // Логика привязки связанных объектов
        if (coordinatesId != null) {
            Coordinates coordinates = coordinatesRepository.findById(coordinatesId)
                    .orElseThrow(() -> new RuntimeException("Coordinates not found"));
            studyGroup.setCoordinates(coordinates);
        }

        if (adminId != null) {
            Person admin = personRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Person not found"));
            studyGroup.setGroupAdmin(admin);
        }

        return studyGroupRepository.save(studyGroup);
    }

    // Получение группы по ID с проверкой доступа
    public StudyGroup getStudyGroupById(Long id) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudyGroup not found"));
        return studyGroup;
    }

    public Page<StudyGroup> getAllStudyGroups(Pageable pageable) {
        return studyGroupRepository.findAll(pageable);
    }


    // Обновление группы
    public StudyGroup updateStudyGroup(Long id, StudyGroup updatedStudyGroup) {
        StudyGroup existingStudyGroup = getStudyGroupById(id);
        checkAccess(existingStudyGroup);

        // Обновляем только разрешённые поля
        existingStudyGroup.setName(updatedStudyGroup.getName());
        existingStudyGroup.setCoordinates(updatedStudyGroup.getCoordinates());
        existingStudyGroup.setStudentsCount(updatedStudyGroup.getStudentsCount());
        existingStudyGroup.setExpelledStudents(updatedStudyGroup.getExpelledStudents());
        existingStudyGroup.setTransferredStudents(updatedStudyGroup.getTransferredStudents());
        existingStudyGroup.setFormOfEducation(updatedStudyGroup.getFormOfEducation());
        existingStudyGroup.setShouldBeExpelled(updatedStudyGroup.getShouldBeExpelled());
        existingStudyGroup.setSemesterEnum(updatedStudyGroup.getSemesterEnum());
        existingStudyGroup.setGroupAdmin(updatedStudyGroup.getGroupAdmin());

        return studyGroupRepository.save(existingStudyGroup);
    }

    // Удаление группы
    public void deleteStudyGroup(Long id) {
        StudyGroup studyGroup = getStudyGroupById(id);
        checkAccess(studyGroup);

        // Проверка на связанные объекты
        if (studyGroup.getCoordinates() != null) {
            throw new RuntimeException("Cannot delete StudyGroup with linked Coordinates");
        }

        studyGroupRepository.deleteById(id);
    }

    // Найти группу с минимальным expelledStudents
    public StudyGroup getStudyGroupWithMinExpelledStudents() {
        return studyGroupRepository.findWithMinExpelledStudents()
                .orElseThrow(() -> new RuntimeException("No StudyGroups found"));
    }

    // Посчитать группы с adminId больше указанного
    public long countStudyGroupsWithAdminGreaterThan(Long adminId) {
        return studyGroupRepository.countByGroupAdminGreaterThan(adminId);
    }

    // Найти группы с подстрокой в имени
    public List<StudyGroup> getStudyGroupsByNameSubstring(String substring, Pageable pageable) {
        return studyGroupRepository.findByNameContaining(substring, pageable);
    }

    // Отчислить всех студентов
    public void expelAllStudents(Long groupId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);
        checkAccess(studyGroup);

        studyGroup.setExpelledStudents((int) (studyGroup.getExpelledStudents() + studyGroup.getStudentsCount()));
        studyGroup.setStudentsCount(0);

        studyGroupRepository.save(studyGroup);
    }

    // Добавить студента
    public void addStudentToGroup(Long groupId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);
        checkAccess(studyGroup);

        studyGroup.setStudentsCount(studyGroup.getStudentsCount() + 1);
        studyGroupRepository.save(studyGroup);
    }

    // Проверка доступа
    private void checkAccess(StudyGroup studyGroup) {
        var currentUser = userService.getCurrentUser();
        if (!studyGroup.getUser().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new RuntimeException("Access denied");
        }
    }

}
