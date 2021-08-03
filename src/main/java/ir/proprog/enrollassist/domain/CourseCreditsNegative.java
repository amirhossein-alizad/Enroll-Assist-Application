package ir.proprog.enrollassist.domain;

public class CourseCreditsNegative extends CourseRuleViolation{
    public CourseCreditsNegative() {}

    @Override
    public String toString() {return "Course credit units cannot be negative.";}
}
