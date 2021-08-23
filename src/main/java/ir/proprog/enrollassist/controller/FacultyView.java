package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Faculty;
import ir.proprog.enrollassist.domain.Major;
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
