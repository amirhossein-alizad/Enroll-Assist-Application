package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseTitleEmpty extends CourseException {
    public CourseTitleEmpty(){}

    @Override
    public String toString() {return "Course must have a name.";}
}
