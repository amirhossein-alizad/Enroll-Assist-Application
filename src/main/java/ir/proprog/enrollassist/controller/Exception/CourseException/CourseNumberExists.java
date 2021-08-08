package ir.proprog.enrollassist.controller.Exception.CourseException;

import ir.proprog.enrollassist.controller.Exception.CourseException.CourseException;

public class CourseNumberExists extends CourseException {
    public CourseNumberExists(){}

    @Override
    public String toString(){return "Course number already exists.";}
}
