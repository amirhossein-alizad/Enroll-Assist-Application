package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.CourseMajorView;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AddCourseService {
    private CourseRepository courseRepository;
    private MajorRepository majorRepository;
    private FacultyRepository facultyRepository;


    public Course addCourse(CourseMajorView input, Faculty faculty) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (courseRepository.findCourseByCourseNumber(input.getCourseNumber()).isPresent())
            exceptionList.addNewException(new Exception("Course number already exists."));

        Course course = null;
        Set<Course> prerequisites = new HashSet<>();
        Set<Major> majors = new HashSet<>();

        try {
            majors = getMajors(input.getMajors(), faculty);
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        try {
            prerequisites = this.validatePrerequisites(input.getPrerequisites());
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        try {
            course = new Course(input.getCourseNumber().getCourseNumber(), input.getCourseTitle(), input.getCourseCredits(), input.getEducationGrade().getGrade());
            course.setPrerequisites(prerequisites);
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }

        if (exceptionList.hasException())
            throw exceptionList;


        addCourseToMajors(course, majors, faculty);
        return course;
    }

    private void addCourseToMajors(Course course, Set<Major> majors, Faculty faculty) {
        this.courseRepository.save(course);
        for (Major major: majors) {
            major.addCourse(course);
            faculty.changeMajor(major);
            this.majorRepository.save(major);
        }
        this.facultyRepository.save(faculty);
    }

    private Set<Course> validatePrerequisites(Set<Long> prerequisiteIds) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        Set<Course> prerequisites = new HashSet<>();
        for(Long L : prerequisiteIds){
            Optional<Course> pre = courseRepository.findById(L);
            if(pre.isEmpty())
                exceptionList.addNewException(new Exception(String.format("Course with id = %s was not found.", L)));
            else {
                try {
                    this.checkLoop(pre.get());
                    prerequisites.add(pre.get());
                }catch (ExceptionList e) {
                    exceptionList.addExceptions(e.getExceptions());
                }
            }
        }
        if (exceptionList.hasException())
            throw exceptionList;
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
