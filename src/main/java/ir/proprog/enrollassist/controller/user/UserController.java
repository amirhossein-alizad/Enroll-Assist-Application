package ir.proprog.enrollassist.controller.user;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.student.StudentView;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserRepository userRepository;
    private StudentRepository studentRepository;

    @GetMapping
    public Iterable<UserView> all() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).map(UserView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserView one(@PathVariable Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserView(user);
    }

    @PostMapping
    public UserView addOne(@RequestBody UserView userView) {
        ExceptionList exceptionList = new ExceptionList();
        User user = null;
        try {
            user = new User(userView.getName(), userView.getUserId());
        } catch (IllegalArgumentException ex) {
            exceptionList.addNewException(ex);
        }
        if (this.userRepository.findByUserId(userView.getUserId()).isPresent())
            exceptionList.addNewException(new Exception("User Id already exists."));

        if (exceptionList.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        return new UserView(user);
    }

    @GetMapping("/{id}/students")
    public Iterable<StudentView> getStudents(@PathVariable Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return user.getStudents().stream().map(StudentView::new).collect(Collectors.toList());
    }

    @PutMapping("/{id}/students/{studentId}")
    public Iterable<StudentView> addStudent(@PathVariable Long id,@PathVariable Long studentId) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Student student = this.studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (userRepository.findByStudentId(studentId).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This student is taken by a user.");
        try {
            user.addStudent(student);
        } catch(Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        userRepository.save(user);
        return user.getStudents().stream().map(StudentView::new).collect(Collectors.toList());
    }
}

