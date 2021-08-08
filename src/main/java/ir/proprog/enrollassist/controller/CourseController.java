package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
@RestController
@RequestMapping("/courses")
public class CourseController {
    private CourseRepository courseRepository;
    List<CourseRuleViolation> courseRuleViolations;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Iterable<CourseView> all() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false).map(CourseView::new).collect(Collectors.toList());
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseValidationResultView addNewCourse(@RequestBody CourseView courseView, HttpServletResponse response){
        courseRuleViolations = new ArrayList<>();
        Course newCourse = new Course(courseView.getCourseNumber(), courseView.getCourseTitle(), courseView.getCourseCredits());
        for(Long L : courseView.getPrerequisites()){
            Optional<Course> prerequisite = courseRepository.findById(L);
            if(prerequisite.isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                courseRuleViolations.add(new PrerequisiteCourseNotFound(L.intValue()));
                continue;
            }
            newCourse.withPre(prerequisite.get());
        }
        if(courseRuleViolations.size() != 0)
            return new CourseValidationResultView(courseRuleViolations);

        validateCourse(newCourse);
        if(courseRuleViolations.isEmpty()) {
            courseRepository.save(newCourse);
            courseRuleViolations.add(new CourseValidationSuccessful(newCourse.getTitle()));
        }
        else
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new CourseValidationResultView(courseRuleViolations);
    }

    private void validateCourse(Course newCourse){
        validateCourseNumber(newCourse.getCourseNumber());
        validateCourseTitle(newCourse.getTitle());
        validateCourseCredits(newCourse.getCredits());
        for(Course p : newCourse.getPrerequisites())
            validatePrerequisites(p);
    }

    private void validateCourseNumber(String courseNumber){
        if(courseNumber.equals(""))
            this.courseRuleViolations.add(new CourseNumberEmpty());
        else {
            if (courseRepository.findCourseByCourseNumber(courseNumber).isPresent())
                this.courseRuleViolations.add(new CourseNumberExists());
        }
    }
    private void validatePrerequisites(Course prerequisite){
        Stack<Course> courseStack = new Stack<>();
        List<Course> courseList = new ArrayList<>();
        courseStack.push(prerequisite);
        while(!courseStack.isEmpty()){
            Course c = courseStack.pop();
            if(courseList.contains(c))
                this.courseRuleViolations.add(new PrerequisitesHaveLoop(c.getTitle()));
            else{
                for(Course p : c.getPrerequisites())
                    courseStack.push(p);
                courseList.add(c);
            }
        }
    }
    private void validateCourseTitle(String title) {
        if (title.equals(""))
            this.courseRuleViolations.add(new CourseTitleEmpty());
    }

    private void validateCourseCredits(int credits){
        if(credits < 0)
            this.courseRuleViolations.add(new CourseCreditsNegative());
    }

    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return new CourseView(course);
    }
}
