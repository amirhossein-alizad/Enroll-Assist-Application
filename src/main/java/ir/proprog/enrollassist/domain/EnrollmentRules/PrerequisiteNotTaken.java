package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.course.Course;

import java.util.Objects;

public class PrerequisiteNotTaken extends EnrollmentRuleViolation {
    private Course requestedCourse;
    private Course prerequisite;

    public PrerequisiteNotTaken(Course requestedCourse, Course prerequisite) {
        this.requestedCourse = requestedCourse;
        this.prerequisite = prerequisite;
    }

    public Course getRequestedCourse() {
        return requestedCourse;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    @Override
    public String toString() {
        return String.format("%s is not passed as a prerequisite of %s", prerequisite, requestedCourse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrerequisiteNotTaken that = (PrerequisiteNotTaken) o;
        return Objects.equals(requestedCourse, that.requestedCourse) &&
                Objects.equals(prerequisite, that.prerequisite);
    }
}
