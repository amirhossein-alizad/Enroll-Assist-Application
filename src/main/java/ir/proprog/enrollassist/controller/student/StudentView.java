package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.student.Student;
import lombok.Getter;
@Getter
public class StudentView {
    private Long studentId;
    private String studentNo;
    private String name;
    private GraduateLevel graduateLevel;
    private Long userId;

    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber().getNumber();
        this.name = student.getName();
        if (student.getGraduateLevel() != null)
            this.graduateLevel = student.getGraduateLevel();

    }

    public void setUserId(Long id){
        userId = id;
    }
}
