package ir.proprog.enrollassist.domain;

public class CourseValidationSuccessful extends CourseRuleViolation{
    String course;
    public CourseValidationSuccessful(String course){this.course = course;}

    @Override
    public String toString() {return String.format("%s was successfully added to courses list.", course);}
}
