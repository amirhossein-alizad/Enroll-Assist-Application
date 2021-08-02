package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping(value = "/addNewCourse", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseView Add_new_course(@RequestBody CourseView courseView){
        Course newCourse = new Course(courseView.getCourseNumber(), courseView.getCourseTitle(), courseView.getCourseCredits());
        for(Long L : courseView.getPrerequisites()){
            Course prerequisite = courseRepository.findById(L)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prerequisite course not found"));
            newCourse.withPre(prerequisite);
        }
        courseRepository.save(newCourse);
        return new CourseView(newCourse);
    }
}
