package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;

import static org.mockito.Mockito.mock;

public class TestStudentBuilder {
    private static String STUDENT_ID;
    private String name;
    private Major major;
    private String graduateLevel;
    private String studentNumber;

    public TestStudentBuilder()
    {
        name = "bebe";
        major = null;
        graduateLevel = "Bs.c";
        studentNumber = "810190000";
    }

    public TestStudentBuilder byGraduateLevel(String _graduateLevel) {
        graduateLevel = _graduateLevel;
        return this;
    }

    public TestStudentBuilder byName(String _name) {
        name = _name;
        return this;
    }

    public TestStudentBuilder byStudentNumber(String _studentNumber) {
        studentNumber = _studentNumber;
        return this;
    }

    public TestStudentBuilder byMajor(Major _major) {
        major = _major;
        return this;
    }

    public Student buildMock() {
        return mock(Student.class);
    }

    public Student build() throws ExceptionList {
        return new Student(studentNumber, name, major, graduateLevel);
    }
}
