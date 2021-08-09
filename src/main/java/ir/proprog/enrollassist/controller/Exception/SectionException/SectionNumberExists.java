package ir.proprog.enrollassist.controller.Exception.SectionException;

public class SectionNumberExists extends SectionException {
    public SectionNumberExists() {}

    @Override
    public String getMessage() {return "This section already exists.";}
}
