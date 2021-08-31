package ir.proprog.enrollassist.controller.faculty;

import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FacultyView {
    private Long facultyId;
    private String facultyName;
    private List<MajorView> majors;

    public FacultyView() {
    }

    public FacultyView(Faculty faculty) {
        this.facultyId = faculty.getId();
        this.facultyName = faculty.getFacultyName();
        majors = faculty.getMajors().stream().map(MajorView::new).collect(Collectors.toList());
    }
}
