package ir.proprog.enrollassist.domain.course;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class AddCourseService {
    private CourseRepository courseRepository;
    private ProgramRepository programRepository;


    public Course addCourse(CourseMajorView input) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (courseRepository.findCourseByCourseNumber(input.getCourseNumber()).isPresent())
            exceptionList.addNewException(new Exception("Course number already exists."));

        Course course = null;
        Set<Course> prerequisites = new HashSet<>();
        Set<Program> programs = new HashSet<>();

        try {
            programs = getPrograms(input.getPrograms());
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        try {
            prerequisites = this.validatePrerequisites(input.getPrerequisites());
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        try {
            course = new Course(input.getCourseNumber().getCourseNumber(), input.getCourseTitle(), input.getCourseCredits(), input.getGraduateLevel().toString());
            course.setPrerequisites(prerequisites);
        } catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }

        if (exceptionList.hasException())
            throw exceptionList;

        for(Program p:programs) {
            try {
                p.addCourse(course);
                this.programRepository.save(p);
            } catch (ExceptionList ex) {
                exceptionList.addExceptions(ex.getExceptions());
            }
        }
        if (exceptionList.hasException())
            throw exceptionList;

        this.courseRepository.save(course);
        return course;
    }

    private Set<Course> validatePrerequisites(Set<Long> prerequisiteIds) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        Set<Course> prerequisites = new HashSet<>();
        prerequisiteIds.forEach(L -> courseRepository.findById(L).
                ifPresentOrElse(c -> checkLoop(c, exceptionList), () -> exceptionList.addNewException(new Exception(String.format("Course with id = %s was not found.", L)))));
        if (exceptionList.hasException())
            throw exceptionList;
        courseRepository.findAllById(prerequisiteIds).forEach(prerequisites::add);
        return prerequisites;
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

    public Set<Program> getPrograms(Set<Long> ids) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        Set<Program> programs = new HashSet<>();
        ids.forEach(L -> programRepository.findById(L).
                ifPresentOrElse(programs::add,
                        () -> exceptionList.addNewException(new Exception(String.format("Program with id = %s was not found.", L)))));
        if (exceptionList.hasException())
            throw exceptionList;
        return programs;
    }
}
