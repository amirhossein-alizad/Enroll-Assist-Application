package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.domain.Student;
import lombok.Getter;

public class StudentView {
    private Long studentId;
    @Getter private String studentNo;
    @Getter private String name;
    @Getter private Major major;

    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber().getNumber();
        this.name = student.getName();
        this.major = student.getMajor();
    }
}
