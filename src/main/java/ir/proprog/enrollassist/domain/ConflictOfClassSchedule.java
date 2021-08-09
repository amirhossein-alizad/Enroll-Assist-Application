package ir.proprog.enrollassist.domain;

public class ConflictOfClassSchedule extends EnrollmentRuleViolation{
    private Course firstCourseTitle;
    private Course secondCourseTitle;

    public ConflictOfClassSchedule(Course firstCourseTitle, Course secondCourseTitle) {
        this.firstCourseTitle = firstCourseTitle;
        this.secondCourseTitle = secondCourseTitle;
    }

    @Override
    public String toString() {
        return String.format("%s course and %s course have conflict in schedule.", this.firstCourseTitle, this.secondCourseTitle);
    }

}
