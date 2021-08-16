package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CourseMajorView {
    private CourseView course = null;
    private Set<Long> majors = new HashSet<>();

    public CourseMajorView() {
    }

    public CourseMajorView(Course course, Set<Long> prerequisites, Set<Long> majors) {
        this.course = new CourseView(course, prerequisites);
        this.majors = majors;
    }
}
