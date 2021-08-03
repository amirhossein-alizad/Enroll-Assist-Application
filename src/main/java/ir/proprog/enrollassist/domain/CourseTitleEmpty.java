package ir.proprog.enrollassist.domain;

public class CourseTitleEmpty extends CourseRuleViolation{
    public CourseTitleEmpty(){}

    @Override
    public String toString() {return "Course must have a name.";}
}
