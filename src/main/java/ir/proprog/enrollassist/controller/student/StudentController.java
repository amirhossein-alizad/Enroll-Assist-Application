package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.controller.student.StudentView;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.domain.user.User;
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
    private UserRepository userRepository;

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
        User user = userRepository.findById(studentView.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + studentView.getUserId() + " was not found."));

        Optional<Student> student = this.studentRepository.findByStudentNumber(new StudentNumber(studentView.getStudentNo()));

        Major major = this.majorRepository.findById(studentView.getMajorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Major not found"));

        if (student.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This student already exists.");
        Student newStudent;
        try {
            newStudent = new Student(studentView.getStudentNo(), studentView.getName(), major, studentView.getGraduateLevel().toString());
        } catch (ExceptionList exceptionList) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        }
        try {
            user.addStudent(newStudent);
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        studentRepository.save(newStudent);
        userRepository.save(user);
        StudentView studentView1 = new StudentView(newStudent);
        studentView1.setUserId(user.getId());
        return studentView1;
    }

    @GetMapping("/{studentNo}/takeableByMajor")
    public Iterable<SectionView> findTakeableSectionsByMajor(@PathVariable String studentNo) {
        Student student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        Iterable<Section> takeable = student.getTakeableSections(sectionRepository.findAll());
        return StreamSupport.stream(takeable.spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }
}
