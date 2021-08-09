package ir.proprog.enrollassist.controller.Exception.SectionException;

public class InvalidSectionNumber extends SectionException {
    public InvalidSectionNumber() {}

    @Override
    public String getMessage() {return "Section number is not valid.";}
}
