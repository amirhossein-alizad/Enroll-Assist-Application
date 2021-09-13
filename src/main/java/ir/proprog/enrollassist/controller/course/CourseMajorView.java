package ir.proprog.enrollassist.controller.course;

import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.course.CourseNumber;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CourseMajorView {
    private Long courseId;
    private CourseNumber courseNumber;
    private GraduateLevel graduateLevel;
    private String courseTitle;
    private int courseCredits;
    private Set<Long> prerequisites = new HashSet<>();
    private Set<Long> programs = new HashSet<>();

    public CourseMajorView() {
    }

    public CourseMajorView(Course course, Set<Long> prerequisites, Set<Long> programs) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        this.graduateLevel = course.getGraduateLevel();
        if(!course.getPrerequisites().isEmpty())
            for(Course c : course.getPrerequisites())
                prerequisites.add(c.getId());
        this.prerequisites = prerequisites;
        this.programs = programs;
    }
}
