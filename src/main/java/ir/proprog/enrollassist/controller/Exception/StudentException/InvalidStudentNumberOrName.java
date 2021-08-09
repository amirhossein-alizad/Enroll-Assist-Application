package ir.proprog.enrollassist.controller.Exception.StudentException;

public class InvalidStudentNumberOrName extends StudentException {
    public InvalidStudentNumberOrName() {}

    @Override
    public String getMessage() {return "Student number or student name is not valid.";}
}
