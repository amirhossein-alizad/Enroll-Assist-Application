package ir.proprog.enrollassist.domain.course;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AddCourseService {
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;


    public Course addCourse(CourseMajorView input) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (courseRepository.findCourseByCourseNumber(input.getCourseNumber()).isPresent())
            exceptionList.addNewException(new Exception("Course number already exists."));

        Course course = null;
        Set<Program> programs = getPrograms(input.getPrograms(), exceptionList);
        this.validatePrerequisites(input.getPrerequisites(), exceptionList);
        Set<Course> prerequisites = this.getPrerequisites(input.getPrerequisites());

        try {
            course = new Course(input.getCourseNumber().getCourseNumber(), input.getCourseTitle(), input.getCourseCredits(), input.getGraduateLevel().toString());
            course.setPrerequisites(prerequisites);
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }

        if (exceptionList.hasException())
            throw exceptionList;

        Course finalCourse = course;
        programs.forEach(p -> addCourseToProgram(p, finalCourse, exceptionList));
        if (exceptionList.hasException())
            throw exceptionList;

        this.programRepository.saveAll(programs);
        this.courseRepository.save(course);
        return course;
    }

    private void addCourseToProgram(Program program, Course course, ExceptionList exceptionList) {
        try {
            program.addCourse(course);
        } catch (ExceptionList ex) {
            exceptionList.addExceptions(ex.getExceptions());
        }
    }

    private Set<Course> getPrerequisites(Set<Long> prerequisiteIds) {
        Set<Course> prerequisites = new HashSet<>();
        courseRepository.findAllById(prerequisiteIds).forEach(prerequisites::add);
        return prerequisites;
    }

    private void validatePrerequisites(Set<Long> prerequisiteIds, ExceptionList exceptionList) {
        ExceptionList exceptions = new ExceptionList();
        prerequisiteIds.forEach(L -> courseRepository.findById(L).
                ifPresentOrElse(c -> checkLoop(c, exceptions), () -> exceptions.addNewException(new Exception(String.format("Course with id = %s was not found.", L)))));
        exceptionList.addExceptions(exceptions.getExceptions());
    }

    private void checkLoop(Course prerequisite, ExceptionList exceptions) {
        Stack<Course> courseStack = new Stack<>();
        List<Course> courseList = new ArrayList<>();
        courseStack.push(prerequisite);
        while(!courseStack.isEmpty()){
            Course c = courseStack.pop();
            if(courseList.contains(c))
                exceptions.addNewException(new Exception(String.format("%s has made a loop in prerequisites.", c.getTitle())));
            else{
                courseStack.addAll(c.getPrerequisites());
                courseList.add(c);
            }
        }
    }

    public Set<Program> getPrograms(Set<Long> ids, ExceptionList exceptions) {
        Set<Program> programs = new HashSet<>();
        ExceptionList exceptionList = new ExceptionList();
        ids.forEach(L -> programRepository.findById(L).
                ifPresentOrElse(programs::add,
                        () -> exceptionList.addNewException(new Exception(String.format("Program with id = %s was not found.", L)))));
        exceptions.addExceptions(exceptionList.getExceptions());
        return programs;
    }
}
