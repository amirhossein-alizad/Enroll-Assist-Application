package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Major;

public class MajorView {
    private Long majorId;
    private String majorNumber;
    private String majorName;
    private String facultyName;

    public MajorView() {
    }

    public MajorView(Major major) {
        this.majorId = major.getId();
        this.majorNumber = major.getMajorNumber();
        this.majorName = major.getMajorName();
        this.facultyName = major.getFaculty().getFacultyName();
    }
}
