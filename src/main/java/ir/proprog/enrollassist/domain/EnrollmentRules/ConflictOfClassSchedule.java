package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.Section;

public class ConflictOfClassSchedule extends EnrollmentRuleViolation{
    private Section firstCourseTitle;
    private Section secondCourseTitle;

    public ConflictOfClassSchedule(Section firstCourseTitle, Section secondCourseTitle) {
        this.firstCourseTitle = firstCourseTitle;
        this.secondCourseTitle = secondCourseTitle;
    }

    @Override
    public String toString() {
        return String.format("%s course and %s course have conflict in schedule.", this.firstCourseTitle, this.secondCourseTitle);
    }

}
