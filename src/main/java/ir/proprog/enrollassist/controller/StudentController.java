package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.domain.StudentNumber;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
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
    private EnrollmentListRepository enrollmentListRepository;

    public StudentController(StudentRepository studentRepository, CourseRepository courseRepository, SectionRepository sectionRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

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
        if (student.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This student already exists.");
        try {
            Student newStudent = new Student(studentView.getStudentNo(), studentView.getName());
            this.studentRepository.save(newStudent);
            return new StudentView(newStudent);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student number or student name is not valid.");
        }
    }

    @GetMapping("/{studentNo}/takeable")
    public Iterable<SectionView> findTakeableSections(@PathVariable String studentNo) {
        Student student = this.studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        Iterable<Section> takeable = student.getTakeableSections(sectionRepository.findAll());
        return StreamSupport.stream(takeable.spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }

    @PutMapping("/{studentNo}/friends")
    public void sendFriendshipRequest(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        Student f = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));

        s.getRequested().add(f);
        f.getPending().add(s);

        studentRepository.save(s);
        studentRepository.save(f);
    }

    @DeleteMapping("/{studentNo}/friends")
    public void removeFriendship(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        Student f = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));

        removeFriend(s, f);
        removeFriend(f, s);

        studentRepository.save(f);
        studentRepository.save(s);
    }

    @GetMapping("/{studentNo}/friends")
    public List<StudentView> getAllFriends(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        List<Student> friends = s.getFriends();
        friends.addAll(s.getBlocked());
        friends.addAll(s.getFriends());
        friends.addAll(s.getRequested());
        friends.addAll(s.getPending());

        return friends.stream().map(StudentView::new).collect(Collectors.toList());
    }

    @GetMapping("/{studentNo}/friends/friends")
    public List<StudentView> getFriends(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));

        return s.getFriends().stream().map(StudentView::new).collect(Collectors.toList());
    }


    @GetMapping("/{studentNo}/friends/blocked")
    public List<StudentView> getBlockedFriends(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));

        return s.getBlocked().stream().map(StudentView::new).collect(Collectors.toList());
    }

    @GetMapping("/{studentNo}/friends/pending")
    public List<StudentView> getPendingFriends(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));

        return s.getPending().stream().map(StudentView::new).collect(Collectors.toList());
    }

    @GetMapping("/{studentNo}/friends/requested")
    public List<StudentView> getRequestedFriends(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));

        return s.getRequested().stream().map(StudentView::new).collect(Collectors.toList());
    }


    @PutMapping("/{studentNo}/friends/accept")
    public void acceptFriendship(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        Student f = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));

        s.getPending().remove(f);
        s.getFriends().add(f);

        f.getRequested().remove(s);
        f.getFriends().add(s);

        studentRepository.save(s);
        studentRepository.save(f);
    }

    @PutMapping("/{studentNo}/friends/block")
    public void blockFriend(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        Student f = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));

        s.getFriends().remove(f);
        s.getBlocked().add(f);

        studentRepository.save(s);
    }

    @PutMapping("/{studentNo}/friends/unblock")
    public void unblockFriend(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
        Student f = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));

        s.getBlocked().remove(f);
        s.getFriends().add(f);

        studentRepository.save(s);
    }

    @GetMapping("/{studentNo}/friends/enrollmentLists")
    public List<EnrollmentListView> getFriendsEnrollmentLists(@PathVariable String studentNo) {
        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));

        List<Student> f = new ArrayList<>();
        for (Student student : s.getFriends())
            if (student.getFriends().contains(s))
                f.add(student);

        List<EnrollmentList> lists = new ArrayList<>();
        for (Student student : f)
            lists.addAll(enrollmentListRepository.findByOwner(student));

        return lists.stream().map(EnrollmentListView::new).collect(Collectors.toList());
    }


    public void removeFriend(Student s, Student f) {
        if (s.getRequested().contains(f))
            s.getRequested().remove(f);
        else if (s.getPending().contains(f))
            s.getPending().remove(f);
        else if (s.getFriends().contains(f))
            s.getFriends().remove(f);
        else s.getBlocked().remove(f);
    }
}
