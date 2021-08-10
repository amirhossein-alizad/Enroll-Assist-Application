package ir.proprog.enrollassist.domain.Exception.CourseException;

import ir.proprog.enrollassist.Exception.CourseException;

public class CourseNumberEmpty extends CourseException {
    public CourseNumberEmpty(){}

    @Override
    public String getMessage() {return "Course number cannot be empty.";}
}
