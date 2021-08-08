package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseNumberEmpty extends CourseException {
    public CourseNumberEmpty(){}

    @Override
    public String toString() {return "Course number cannot be empty.";}
}
