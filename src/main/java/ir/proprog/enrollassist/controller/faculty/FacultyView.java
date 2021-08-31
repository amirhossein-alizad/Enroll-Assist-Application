package ir.proprog.enrollassist.controller.faculty;

import ir.proprog.enrollassist.domain.faculty.Faculty;
import lombok.Getter;

@Getter
public class FacultyView {
    private Long facultyId;
    private String facultyName;

    public FacultyView() {
    }

    public FacultyView(Faculty faculty) {
        this.facultyId = faculty.getId();
        this.facultyName = faculty.getFacultyName();
    }
}
