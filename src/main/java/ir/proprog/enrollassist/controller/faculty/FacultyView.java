package ir.proprog.enrollassist.controller.faculty;

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

    public FacultyView() {
    }

    public FacultyView(Faculty faculty) {
        this.facultyId = faculty.getId();
        this.facultyName = faculty.getFacultyName();
    }
}
