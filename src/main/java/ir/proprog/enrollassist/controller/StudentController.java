package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.Exception.StudentException.InvalidStudentNumberOrName;
import ir.proprog.enrollassist.controller.Exception.StudentException.StudentNumberExists;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Student;
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
        Student student = this.studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return new StudentView(student);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public StudentView addStudent(@RequestBody StudentView studentView) {
        Optional<Student> student = this.studentRepository.findByStudentNumber(studentView.getStudentNo());
        ExceptionList exceptionList = new ExceptionList();
        if (student.isPresent()) {
            exceptionList.addNewException(new StudentNumberExists());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,exceptionList.toString());
        }
        try {
            Student newStudent = new Student(studentView.getStudentNo(), studentView.getName());
            this.studentRepository.save(newStudent);
            return new StudentView(newStudent);
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionList.addNewException(new InvalidStudentNumberOrName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,exceptionList.toString());
        }
    }

    @GetMapping("/takeableSections/{studentNumber}")
    public Iterable<SectionView> takeableSections(@PathVariable String studentNumber){
        Optional<Student> student = this.studentRepository.findByStudentNumber(studentNumber);
        if(student.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        Student std = student.get();
        return std.getTakeableSections(courseRepository.findAll(), sectionRepository.findAll());
    }

}
