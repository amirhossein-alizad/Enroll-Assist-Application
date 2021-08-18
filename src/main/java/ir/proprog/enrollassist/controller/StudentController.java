package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.domain.StudentNumber;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
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
@RequestMapping("/student")
public class StudentController {
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private SectionRepository sectionRepository;

    public StudentController(StudentRepository studentRepository, CourseRepository courseRepository, SectionRepository sectionRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping("/all")
    public Iterable<StudentView> all(){
        return StreamSupport.stream(studentRepository.findAll().spliterator(), false).map(StudentView::new).collect(Collectors.toList());
    }

    @GetMapping("/{studentNumber}")
    public StudentView one(@PathVariable String studentNumber) {
        Student student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNumber))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return new StudentView(student);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public StudentView addStudent(@RequestBody StudentView studentView) {
        Optional<Student> student = this.studentRepository.findByStudentNumber(new StudentNumber(studentView.getStudentNo()));
        if (student.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This student already exists.");
        try {
            Student newStudent = new Student(studentView.getStudentNo(), studentView.getName());
            this.studentRepository.save(newStudent);
            return new StudentView(newStudent);
        }catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student number or student name is not valid.");
        }
    }

    @GetMapping("/takeableSections/{studentNumber}")
    public Iterable<SectionView> takeableSections(@PathVariable String studentNumber){
        Optional<Student> student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNumber));
        if(student.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        Student std = student.get();
        Iterable<Section> takeable = std.getTakeableSections(courseRepository.findAll(), sectionRepository.findAll());
        return StreamSupport.stream(takeable.spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/takeableSectionsInStudentMajor/{studentNumber}")
    public Iterable<SectionView> takeableSectionsByMajor(@PathVariable String studentNumber){
        Optional<Student> student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNumber));
        if(student.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        Student std = student.get();
        Iterable<Section> takeable = std.getTakeableSections(std.getMajor().getCourses(), sectionRepository.findAll());
        return StreamSupport.stream(takeable.spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }
}
