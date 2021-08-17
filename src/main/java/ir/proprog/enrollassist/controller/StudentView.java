package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.domain.Student;
import lombok.Getter;

public class StudentView {
    private Long studentId;
    @Getter private String studentNo;
    @Getter private String name;
    private String majorNumber;
    private String majorName;

    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber().getStudentNumber();
        this.name = student.getName();
        if (student.getMajor() != null) {
            this.majorName = student.getMajor().getMajorName();
            this.majorNumber = student.getMajor().getMajorNumber();
        }
    }
}
