package ir.proprog.enrollassist.controller.faculty;

import ir.proprog.enrollassist.domain.faculty.Faculty;
import lombok.Getter;

@Getter
public class FacultyAddView {
    private Long facultyId;
    private String facultyName;

    public FacultyAddView() {
    }

    public FacultyAddView(Faculty faculty) {
        this.facultyId = faculty.getId();
        this.facultyName = faculty.getFacultyName();
    }
}
