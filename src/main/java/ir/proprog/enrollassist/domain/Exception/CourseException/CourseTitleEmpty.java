package ir.proprog.enrollassist.domain.Exception.CourseException;

import ir.proprog.enrollassist.Exception.CourseException;

public class CourseTitleEmpty extends CourseException {
    public CourseTitleEmpty(){}

    @Override
    public String getMessage() {return "Course must have a name.";}
}
