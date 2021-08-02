package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;

public class CourseView {
    private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;

    public CourseView() {
    }

    public CourseView(Course course) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
    }
}
