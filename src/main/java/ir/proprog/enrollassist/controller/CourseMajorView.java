package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import lombok.Getter;

import java.util.Set;

@Getter
public class CourseMajorView {
    private final Long courseId;
    private final String courseNumber;
    private final String courseTitle;
    private final int courseCredits;
    private final Set<Long> prerequisites;
    private final Set<Long> majors;


    public CourseMajorView(Course course, Set<Long> prerequisites, Set<Long> majors) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        this.prerequisites = prerequisites;
        this.majors = majors;
    }
}
