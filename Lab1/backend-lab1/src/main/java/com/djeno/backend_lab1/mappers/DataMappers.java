package com.djeno.backend_lab1.mappers;

import com.djeno.backend_lab1.DTO.AdminRequestResponseDTO;
import com.djeno.backend_lab1.DTO.StudyGroupHistoryResponseDTO;
import com.djeno.backend_lab1.DTO.StudyGroupResponseDTO;
import com.djeno.backend_lab1.DTO.UserDTO;
import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.StudyGroupHistory;
import com.djeno.backend_lab1.models.User;

public class DataMappers {

    // Маппинг User -> UserDTO
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

    // Маппинг StudyGroup -> StudyGroupResponseDTO
    public static StudyGroupResponseDTO toStudyGroupResponseDTO(StudyGroup studyGroup) {
        if (studyGroup == null) {
            return null;
        }

        StudyGroupResponseDTO dto = new StudyGroupResponseDTO();

        dto.setId(studyGroup.getId());
        dto.setName(studyGroup.getName());
        dto.setCoordinates(studyGroup.getCoordinates());
        dto.setCreationDate(studyGroup.getCreationDate());
        dto.setStudentsCount(studyGroup.getStudentsCount());
        dto.setExpelledStudents(studyGroup.getExpelledStudents());
        dto.setTransferredStudents(studyGroup.getTransferredStudents());
        dto.setFormOfEducation(studyGroup.getFormOfEducation());
        dto.setShouldBeExpelled(studyGroup.getShouldBeExpelled());
        dto.setSemesterEnum(studyGroup.getSemesterEnum());
        dto.setGroupAdmin(studyGroup.getGroupAdmin());

        // Используем метод для маппинга User -> UserDTO
        dto.setUser(toUserDTO(studyGroup.getUser()));

        return dto;
    }

    // Преобразование StudyGroupHistory -> StudyGroupHistoryResponseDTO
    public static StudyGroupHistoryResponseDTO toStudyGroupHistoryResponseDTO(StudyGroupHistory history) {
        if (history == null) {
            return null;
        }

        StudyGroupHistoryResponseDTO dto = new StudyGroupHistoryResponseDTO();
        dto.setId(history.getId());
        dto.setStudyGroupId(history.getStudyGroupId());
        dto.setName(history.getName());
        dto.setCoordinates(history.getCoordinates());
        dto.setGroupAdmin(history.getGroupAdmin());
        dto.setStudentsCount(history.getStudentsCount());
        dto.setExpelledStudents(history.getExpelledStudents());
        dto.setTransferredStudents(history.getTransferredStudents());
        dto.setFormOfEducation(history.getFormOfEducation());
        dto.setSemesterEnum(history.getSemesterEnum());
        dto.setShouldBeExpelled(history.getShouldBeExpelled());
        dto.setVersion(history.getVersion());
        dto.setUpdatedAt(history.getUpdatedAt());

        // Маппинг User -> UserDTO
        dto.setUpdatedBy(toUserDTO(history.getUpdatedBy()));

        return dto;
    }

    public static AdminRequestResponseDTO toAdminRequestResponseDTO(AdminRequest adminRequest) {
        if (adminRequest == null) {
            return null;
        }

        AdminRequestResponseDTO dto = new AdminRequestResponseDTO();
        dto.setId(adminRequest.getId());
        dto.setStatus(adminRequest.getStatus());
        dto.setCreatedAt(adminRequest.getCreatedAt());

        // Маппинг User -> UserDTO
        dto.setUser(toUserDTO(adminRequest.getUser()));

        return dto;
    }
}
