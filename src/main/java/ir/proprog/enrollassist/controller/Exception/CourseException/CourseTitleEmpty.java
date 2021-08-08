package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseTitleEmpty extends CourseException {
    public CourseTitleEmpty(){}

    @Override
    public String getMessage() {return "Course must have a name.";}
}
