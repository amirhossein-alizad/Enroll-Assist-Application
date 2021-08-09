package ir.proprog.enrollassist.domain.Exception.CourseException;

import ir.proprog.enrollassist.Exception.CourseException;

public class CourseCreditsNegative extends CourseException {
    public CourseCreditsNegative() {}

    @Override
    public String getMessage() {return "Course credit units cannot be negative.";}
}
