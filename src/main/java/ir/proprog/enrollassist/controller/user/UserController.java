package ir.proprog.enrollassist.controller.user;

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

    @GetMapping
    public Iterable<UserView> all() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).map(UserView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserView one(@PathVariable String id) {
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserView(user);
    }
}
