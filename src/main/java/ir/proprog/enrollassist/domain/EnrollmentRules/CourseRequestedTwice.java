package ir.proprog.enrollassist.domain.EnrollmentRules;

import ir.proprog.enrollassist.domain.course.Course;

import java.util.Objects;

public class CourseRequestedTwice extends EnrollmentRuleViolation {

    private final Course course;

    public CourseRequestedTwice(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return this.course;
    }

    @Override
    public String toString() {
        return String.format("%s is requested to be taken twice", getCourse());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseRequestedTwice that = (CourseRequestedTwice) o;
        return Objects.equals(course, that.course);
    }
}
