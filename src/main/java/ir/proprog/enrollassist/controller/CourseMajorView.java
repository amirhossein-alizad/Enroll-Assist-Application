package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.CourseNumber;
import ir.proprog.enrollassist.domain.EducationGrade;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CourseMajorView {
    private Long courseId;
    private CourseNumber courseNumber;
    private EducationGrade educationGrade;
    private String courseTitle;
    private int courseCredits;
    private Set<Long> prerequisites = new HashSet<>();
    private Set<Long> majors = new HashSet<>();

    public CourseMajorView() {
    }

    public CourseMajorView(Course course, Set<Long> prerequisites, Set<Long> majors) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        this.educationGrade = course.getEducationGrade();
        if(!course.getPrerequisites().isEmpty())
            for(Course c : course.getPrerequisites())
                prerequisites.add(c.getId());
        this.prerequisites = prerequisites;
        this.majors = majors;
    }
}
