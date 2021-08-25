package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.CourseMajorView;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddCourseService {
    private CourseRepository courseRepository;
    private MajorRepository majorRepository;

    public AddCourseService(CourseRepository courseRepository, MajorRepository majorRepository) {
        this.courseRepository = courseRepository;
        this.majorRepository = majorRepository;
    }

    public Course addCourse(CourseMajorView input) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (courseRepository.findCourseByCourseNumber(input.getCourseNumber()).isPresent())
            exceptionList.addNewException(new Exception("Course number already exists."));
        Course course = null;
        Set<Course> prerequisites = this.validatePrerequisites(input.getPrerequisites(), exceptionList);
        try {
            course = new Course(input.getCourseNumber(), input.getCourseTitle(), input.getCourseCredits());
            course.setPrerequisites(prerequisites);
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        if (exceptionList.hasException())
            throw exceptionList;

        return course;
    }

    private Set<Course> validatePrerequisites(Set<Long> prerequisiteIds, ExceptionList exceptions) {
        Set<Course> prerequisites = new HashSet<>();
        for(Long L : prerequisiteIds){
            Optional<Course> pre = courseRepository.findById(L);
            if(pre.isEmpty())
                exceptions.addNewException(new Exception(String.format("Course with id = %s was not found.", L)));
            else {
                try {
                    this.checkLoop(pre.get());
                    prerequisites.add(pre.get());
                }catch (ExceptionList e) {
                    exceptions.addExceptions(e.getExceptions());
                }
            }
        }
        return prerequisites;
    }

    private void checkLoop(Course prerequisite) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        Stack<Course> courseStack = new Stack<>();
        List<Course> courseList = new ArrayList<>();
        courseStack.push(prerequisite);
        while(!courseStack.isEmpty()){
            Course c = courseStack.pop();
            if(courseList.contains(c))
                exceptionList.addNewException(new Exception(String.format("%s has made a loop in prerequisites.", c.getTitle())));
            else{
                for(Course p : c.getPrerequisites())
                    courseStack.push(p);
                courseList.add(c);
            }
        }
        if (exceptionList.hasException())
            throw exceptionList;
    }

    public Set<Major> getMajors(Set<Long> majorIds, Faculty faculty) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        Set<Major> majors = new HashSet<>();
        for (Long L : majorIds) {
            Optional<Major> major = majorRepository.findById(L);
            if (major.isEmpty())
                exceptionList.addNewException(new Exception(String.format("Major with id = %s was not found.", L)));
            else if (!faculty.getMajors().contains(major.get()))
                exceptionList.addNewException(new Exception(String.format("Major with id = %s not belong to this faculty.", L)));
            else
                majors.add(major.get());
        }
        if (exceptionList.hasException())
            throw exceptionList;
        return majors;
    }
}
