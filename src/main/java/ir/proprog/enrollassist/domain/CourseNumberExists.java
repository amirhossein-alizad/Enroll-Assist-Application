package ir.proprog.enrollassist.domain;

public class CourseNumberExists extends CourseRuleViolation{
    public CourseNumberExists(){}

    @Override
    public String toString(){return "Course number already exists.";}
}
