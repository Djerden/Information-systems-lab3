package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.StudyGroupHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupHistoryRepository extends JpaRepository<StudyGroupHistory, Long> {

    List<StudyGroupHistory> findByStudyGroupIdOrderByVersionDesc(Long studyGroupId);

}
