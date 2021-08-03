package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{studentNumber}")
    public StudentView one(@PathVariable String studentNumber) {
        Student student = this.studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return new StudentView(student);
    }

    @PostMapping(value = "/addStudent", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Student addStudent(@RequestBody StudentView studentView) {
        this.studentRepository.findByStudentNumber(studentView.getStudentNo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "This section is already exists."));
        try {
            return new Student(studentView.getStudentNo(), studentView.getName());
        }catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student number or student name is not valid.");
        }
    }

}
