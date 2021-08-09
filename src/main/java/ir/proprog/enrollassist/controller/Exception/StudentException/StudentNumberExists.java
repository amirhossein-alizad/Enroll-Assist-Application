package ir.proprog.enrollassist.controller.Exception.StudentException;

public class StudentNumberExists extends StudentException {
    public StudentNumberExists() {}

    @Override
    public String getMessage() {return "This student already exists.";}
}
