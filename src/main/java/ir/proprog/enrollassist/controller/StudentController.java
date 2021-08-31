package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.domain.StudentNumber;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private SectionRepository sectionRepository;
    private EnrollmentListRepository enrollmentListRepository;
    private MajorRepository majorRepository;

    @GetMapping("/all")
    public Iterable<StudentView> all() {
        return StreamSupport.stream(studentRepository.findAll().spliterator(), false).map(StudentView::new).collect(Collectors.toList());
    }

    @GetMapping("/{studentNumber}")
    public StudentView one(@PathVariable String studentNumber) {
        Student student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNumber))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return new StudentView(student);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentView addStudent(@RequestBody StudentView studentView) {
        Optional<Student> student = this.studentRepository.findByStudentNumber(new StudentNumber(studentView.getStudentNo()));

        Major major = this.majorRepository.findById(studentView.getMajorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Major not found"));

        if (student.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This student already exists.");
        try {
            Student newStudent = new Student(studentView.getStudentNo(), studentView.getName(), major, studentView.getEducationGrade());
            this.studentRepository.save(newStudent);
            return new StudentView(newStudent);
        } catch (ExceptionList exceptionList) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        }
    }

    @GetMapping("/{studentNo}/takeableByMajor")
    public Iterable<SectionView> findTakeableSectionsByMajor(@PathVariable String studentNo) {
        Student student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        Iterable<Section> takeable = student.getTakeableSections(sectionRepository.findAll());
        return StreamSupport.stream(takeable.spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }
}
