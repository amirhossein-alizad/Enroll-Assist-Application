package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Student;

public class StudentView {
    private Long studentId;
    private String studentNo;
    private String name;
    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber();
        this.name = student.getName();
    }
}
