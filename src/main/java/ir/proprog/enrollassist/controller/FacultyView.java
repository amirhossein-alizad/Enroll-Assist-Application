package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Faculty;
import ir.proprog.enrollassist.domain.Major;
import lombok.Getter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class FacultyView {
    private Long facultyId;
    private String facultyName;
    private Set<Long> majors = new HashSet<>();

    public FacultyView() {
    }

    public FacultyView(Faculty faculty) {
        this.facultyId = faculty.getId();
        this.facultyName = faculty.getFacultyName();
        this.majors = faculty.getMajors().stream().map(Major::getId).collect(Collectors.toSet());
    }
}
