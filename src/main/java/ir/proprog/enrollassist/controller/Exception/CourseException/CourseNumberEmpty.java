package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseNumberEmpty extends CourseException {
    public CourseNumberEmpty(){}

    @Override
    public String getMessage() {return "Course number cannot be empty.";}
}
