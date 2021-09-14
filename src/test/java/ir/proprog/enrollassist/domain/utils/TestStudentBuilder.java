package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.student.Student;

import static org.mockito.Mockito.mock;

public class TestStudentBuilder {
    private Major major;
    private String graduateLevel;
    private String studentNumber;

    public TestStudentBuilder()
    {
        graduateLevel = "PHD";
        studentNumber = "810190000";
    }

    public TestStudentBuilder withGraduateLevel(String _graduateLevel) {
        graduateLevel = _graduateLevel;
        return this;
    }

    public TestStudentBuilder withStudentNumber(String _studentNumber) {
        studentNumber = _studentNumber;
        return this;
    }

    public TestStudentBuilder withMajor(Major _major) {
        major = _major;
        return this;
    }

    public Student buildMock() {
        return mock(Student.class);
    }

    public Student build() throws ExceptionList {
        return new Student(studentNumber, graduateLevel);
    }
}
