package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.domain.student.Student;
import lombok.Getter;

public class StudentView {
    private Long studentId;
    @Getter private String studentNo;
    @Getter private String name;
    @Getter private Long majorId;
    @Getter private String educationGrade;

    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber().getNumber();
        this.name = student.getName();
        if (student.getMajor() != null)
            this.majorId = student.getMajor().getId();
        if (student.getEducationGrade() != null)
            this.educationGrade = student.getEducationGrade().getGrade();

    }
}
