package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Student;
import lombok.Getter;

public class StudentView {
    private Long studentId;
    @Getter private String studentNo;
    @Getter private String name;

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber();
        this.name = student.getName();
    }
}
