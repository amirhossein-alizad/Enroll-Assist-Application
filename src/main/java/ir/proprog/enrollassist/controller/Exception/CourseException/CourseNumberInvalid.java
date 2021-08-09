package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseNumberInvalid extends CourseException {
    public CourseNumberInvalid(){}

    @Override
    public String getMessage(){return "Course number must contain 7 numbers.";}
}
