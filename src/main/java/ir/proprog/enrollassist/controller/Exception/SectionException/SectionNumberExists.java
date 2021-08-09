package ir.proprog.enrollassist.controller.Exception.SectionException;

import ir.proprog.enrollassist.controller.Exception.CourseException.CourseException;

public class SectionNumberExists extends SectionException {
    public SectionNumberExists() {}

    @Override
    public String getMessage() {return "This section already exists.";}
}
