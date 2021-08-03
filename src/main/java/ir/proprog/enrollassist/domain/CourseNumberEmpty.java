package ir.proprog.enrollassist.domain;

public class CourseNumberEmpty extends CourseRuleViolation{
    public CourseNumberEmpty(){}

    @Override
    public String toString() {return "Course number cannot be empty.";}
}
