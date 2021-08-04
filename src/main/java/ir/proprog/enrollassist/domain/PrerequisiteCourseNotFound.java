package ir.proprog.enrollassist.domain;

public class PrerequisiteCourseNotFound extends CourseRuleViolation{
    private int id;

    public PrerequisiteCourseNotFound(int id){this.id = id;}

    @Override
    public String toString() {return String.format("Course with id = %d was not found.", id);}
}
