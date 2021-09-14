package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import lombok.Getter;
@Getter
public class StudentView {
    private Long studentId;
    private StudentNumber studentNo;
    private GraduateLevel graduateLevel;
    private String userId;

    public StudentView() {
    }

    public StudentView(Student student) {
        this.studentId = student.getId();
        this.studentNo = student.getStudentNumber();
        if (student.getGraduateLevel() != null)
            this.graduateLevel = student.getGraduateLevel();

    }

    public void setUserId(String id){
        userId = id;
    }
}
