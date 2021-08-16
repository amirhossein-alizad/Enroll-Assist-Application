package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private CourseRepository courseRepository;
    private FacultyRepository facultyRepository;
    private MajorRepository majorRepository;

    public CourseController(CourseRepository courseRepository, FacultyRepository facultyRepository, MajorRepository majorRepository) {
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.majorRepository = majorRepository;
    }

    @GetMapping
    public Iterable<CourseView> all() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false).map(CourseView::new).collect(Collectors.toList());
    }

    @PostMapping(
            value = "/{facultyId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseView addNewCourse(@PathVariable Long facultyId, @RequestBody CourseMajorView course) {
        facultyRepository.findById(facultyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));
        CourseView courseView = course.getCourse();
        ExceptionList exceptionList = new ExceptionList();
        Set<Course> prerequisites = new HashSet<>();
        CourseView outputCourse = new CourseView();
        if (courseRepository.findCourseByCourseNumber(courseView.getCourseNumber()).isPresent())
            exceptionList.addNewException(new Exception("Course number already exists."));
        try {
            prerequisites = this.validatePrerequisites(courseView.getPrerequisites());
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        try {
            Course newCourse = new Course(courseView.getCourseNumber(), courseView.getCourseTitle(), courseView.getCourseCredits());
            newCourse.setPrerequisites(prerequisites);
            outputCourse = new CourseView(newCourse);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }

        if (exceptionList.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        return outputCourse;
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

    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return new CourseView(course);
    }
}
