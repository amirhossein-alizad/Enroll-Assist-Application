package ir.proprog.enrollassist.domain;

public class WrongCourseNumberFormat extends CourseRuleViolation{
    public WrongCourseNumberFormat() {}

    @Override
    public String toString() {return "Course number must be a number.";}

}

