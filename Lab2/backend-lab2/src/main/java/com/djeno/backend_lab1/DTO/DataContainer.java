package com.djeno.backend_lab1.DTO;

import com.djeno.backend_lab1.models.Coordinates;
import com.djeno.backend_lab1.models.Location;
import com.djeno.backend_lab1.models.Person;
import com.djeno.backend_lab1.models.StudyGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataContainer {
    private List<Coordinates> coordinatesList;
    private List<Location> locationList;
    private List<Person> personList;
    private List<StudyGroup> studyGroupList;

    public DataContainer(List<Coordinates> coordinatesList, List<Location> locationList,
                         List<Person> personList, List<StudyGroup> studyGroupList) {
        this.coordinatesList = coordinatesList;
        this.locationList = locationList;
        this.personList = personList;
        this.studyGroupList = studyGroupList;
    }
}
