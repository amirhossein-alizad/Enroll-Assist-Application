package ir.proprog.enrollassist.controller.Exception;

import ir.proprog.enrollassist.Exception.CourseException;

public class CourseNumberExists extends CourseException {
    public CourseNumberExists(){}

    @Override
    public String getMessage(){return "Course number already exists.";}
}
