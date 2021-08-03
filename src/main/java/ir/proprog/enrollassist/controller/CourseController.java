package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
@RestController
@RequestMapping("/courses")
public class CourseController {
    private CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Iterable<CourseView> all() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false).map(CourseView::new).collect(Collectors.toList());
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseView addNewCourse(@RequestBody CourseView courseView){
        List<CourseRuleViolation> courseRuleViolations = new ArrayList<>();
        Course newCourse = new Course(courseView.getCourseNumber(), courseView.getCourseTitle(), courseView.getCourseCredits());
        courseRuleViolations.addAll(this.ValidateCourseNumber(newCourse.getCourseNumber()));
        courseRuleViolations.addAll(this.ValidateCourseTitle(newCourse.getTitle()));
        for(Long L : courseView.getPrerequisites()){
            Course prerequisite = courseRepository.findById(L)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prerequisite course not found"));
            newCourse.withPre(prerequisite);
        }
        courseRepository.save(newCourse);
        return new CourseView(newCourse);
    }

    private List<CourseRuleViolation> ValidateCourseNumber(String courseNumber){
        List<CourseRuleViolation> courseRuleViolations = new ArrayList<>();
        if(courseNumber.equals(""))
            courseRuleViolations.add(new CourseNumberEmpty());
        else{
            try {
                Integer.parseInt(courseNumber);
                if(courseRepository.FindCourseByCourseNumber(courseNumber).isPresent())
                    courseRuleViolations.add(new CourseNumberExists());
            } catch (NumberFormatException numberFormatException) {
                courseRuleViolations.add(new WrongCourseNumberFormat());
            }
        }
        return courseRuleViolations;
    }

    private List<CourseRuleViolation> ValidateCourseTitle(String courseTitle) {
        List<CourseRuleViolation> courseRuleViolations = new ArrayList<>();
        if (courseTitle.equals(""))
            courseRuleViolations.add(new CourseNumberEmpty());
        return courseRuleViolations;
    }

    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return new CourseView(course);
    }
}
