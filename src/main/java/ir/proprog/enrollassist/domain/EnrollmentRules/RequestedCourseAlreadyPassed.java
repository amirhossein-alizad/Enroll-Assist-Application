package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.course.Course;

import java.util.Objects;

public class RequestedCourseAlreadyPassed extends EnrollmentRuleViolation {
    private Course requestedCourse;

    public RequestedCourseAlreadyPassed(Course requestedCourse) {
        this.requestedCourse = requestedCourse;
    }

    public Course getRequestedCourse() {
        return requestedCourse;
    }

    @Override
    public String toString() {
        return String.format("%s has been already passed", requestedCourse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestedCourseAlreadyPassed that = (RequestedCourseAlreadyPassed) o;
        return Objects.equals(requestedCourse, that.requestedCourse);
    }
}
