package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/all")
    public Iterable<StudentView> all(){
        return StreamSupport.stream(studentRepository.findAll().spliterator(), false).map(StudentView::new).collect(Collectors.toList());
    }
    @GetMapping("/{studentNumber}")
    public StudentView one(@PathVariable String studentNumber) {
        Student student = this.studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return new StudentView(student);
    }

    @PostMapping(value ="/addStudent", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public StudentView addStudent(@RequestBody StudentView studentView) {
        Optional<Student> student = this.studentRepository.findByStudentNumber(studentView.getStudentNo());
        if (student.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This student already exists.");
        try {
            Student newStudent = new Student(studentView.getStudentNo(), studentView.getName());
            this.studentRepository.save(newStudent);
            return new StudentView(newStudent);
        }catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student number or student name is not valid.");
        }
    }

    @GetMapping("/takeableSections/{studentNumber}")
    public Iterable<CourseView> takeableSections(@PathVariable String studentNumber){
        List<CourseView> takeable  = new ArrayList<>();
        List<Course> notPassed = new ArrayList<>();
        courseRepository.findAll().forEach(notPassed::add);
        Optional<Student> student = this.studentRepository.findByStudentNumber(studentNumber);
        if(student.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        Student std = student.get();
        notPassed.removeAll(std.getPassedCourses());
        for(Course c : notPassed)
            if(c.canBeTakenBy(std).isEmpty())
                takeable.add(new CourseView(c));
        return takeable;
    }

}
