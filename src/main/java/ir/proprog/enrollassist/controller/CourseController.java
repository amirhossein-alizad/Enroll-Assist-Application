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

import java.util.HashSet;
import java.util.Set;
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

    @PostMapping(value = "addCourse/{facultyId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseView addNewCourse(@PathVariable Long facultyId, @RequestBody CourseMajorView input) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));

        ExceptionList exceptionList = new ExceptionList();
        AddCourseService addCourseService = new AddCourseService(courseRepository, majorRepository);
        Set<Major> majors = new HashSet<>();
        try {
            majors = addCourseService.getMajors(input.getMajors(), faculty);
        }catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }
        Course course = null;
        try {
            course = addCourseService.addCourse(input);
            this.courseRepository.save(course);
            for (Major major: majors) {
                major.addCourse(course);
                faculty.changeMajor(major);
                this.majorRepository.save(major);
            }
            this.facultyRepository.save(faculty);
        }catch (ExceptionList e) { exceptionList.addExceptions(e.getExceptions()); }

        if (exceptionList.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        return new CourseView(course);
    }


    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return new CourseView(course);
    }
}
