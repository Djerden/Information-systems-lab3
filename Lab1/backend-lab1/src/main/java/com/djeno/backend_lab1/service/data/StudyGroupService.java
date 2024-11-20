package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.models.enums.Semester;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final PersonService personService;
    private final UserService userService;

    public StudyGroup createStudyGroup(StudyGroupDTO studyGroupDTO) {
        var currentUser = userService.getCurrentUser();

        // Извлечение Coordinates по id из DTO
        Coordinates coordinates = coordinatesRepository.findById(studyGroupDTO.getCoordinatesId())
                .orElseThrow(() -> new RuntimeException("Coordinates not found"));

        // Извлечение Admin по id из DTO (если указан)
        Person admin = null;
        if (studyGroupDTO.getGroupAdminId() != null) {
            System.out.println(studyGroupDTO.getGroupAdminId());
            admin = personService.getPersonById(studyGroupDTO.getGroupAdminId());
        }

        // Преобразование DTO в Entity
        StudyGroup studyGroup = fromDTO(studyGroupDTO, coordinates, admin, currentUser);

        // Сохранение StudyGroup
        return studyGroupRepository.save(studyGroup);
    }

    public static StudyGroup fromDTO(StudyGroupDTO dto, Coordinates coordinates, Person admin, User user) {
        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setName(dto.getName());
        studyGroup.setCoordinates(coordinates); // Устанавливаем объект Coordinates
        studyGroup.setCreationDate(LocalDate.now()); // Автоматическая установка даты
        studyGroup.setStudentsCount(dto.getStudentsCount());
        studyGroup.setExpelledStudents(dto.getExpelledStudents());
        studyGroup.setTransferredStudents(dto.getTransferredStudents());
        studyGroup.setFormOfEducation(dto.getFormOfEducation() != null
                ? FormOfEducation.valueOf(dto.getFormOfEducation()) : null);
        studyGroup.setShouldBeExpelled(dto.getShouldBeExpelled());
        studyGroup.setSemesterEnum(dto.getSemesterEnum() != null
                ? Semester.valueOf(dto.getSemesterEnum()) : null);
        studyGroup.setGroupAdmin(admin); // Устанавливаем объект Admin (если есть)
        studyGroup.setUser(user); // Устанавливаем текущего пользователя
        return studyGroup;
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
