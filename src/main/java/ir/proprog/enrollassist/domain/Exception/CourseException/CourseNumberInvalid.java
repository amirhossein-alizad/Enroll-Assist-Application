package ir.proprog.enrollassist.domain.Exception.CourseException;

import ir.proprog.enrollassist.Exception.CourseException;

public class CourseNumberInvalid extends CourseException {
    public CourseNumberInvalid(){}

    @Override
    public String getMessage(){return "Course number must contain 7 numbers.";}
}
