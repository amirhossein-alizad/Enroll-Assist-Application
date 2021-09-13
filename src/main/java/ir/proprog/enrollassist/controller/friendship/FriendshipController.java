//package ir.proprog.enrollassist.controller.friendship;
//
//import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
//import ir.proprog.enrollassist.controller.student.StudentView;
//import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
//import ir.proprog.enrollassist.domain.student.Student;
//import ir.proprog.enrollassist.domain.student.StudentNumber;
//import ir.proprog.enrollassist.repository.EnrollmentListRepository;
//import ir.proprog.enrollassist.repository.StudentRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/friends")
//@AllArgsConstructor
//public class FriendshipController {
//    private StudentRepository studentRepository;
//    private EnrollmentListRepository enrollmentListRepository;
//
//    @PutMapping("/{studentNo}")
//    public void sendFriendshipRequest(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//        Student friend = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));
//
//        try {
//            student.sendFriendshipRequest(friend);
//            friend.receiveFriendshipRequest(student);
//        } catch (Exception exception) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        }
//        studentRepository.save(student);
//        studentRepository.save(friend);
//    }
//
//    @DeleteMapping("/{studentNo}")
//    public void removeFriendship(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//        Student friend = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));
//
//        try {
//            student.removeFriend(friend);
//            friend.removeFriend(student);
//        } catch (Exception exception) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        }
//
//        studentRepository.save(student);
//        studentRepository.save(friend);
//    }
//
//    @GetMapping("/{studentNo}")
//    public List<StudentView> getAllFriends(@PathVariable String studentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        return student.getAllFriends().stream().map(StudentView::new).collect(Collectors.toList());
//    }
//
//    @GetMapping("/{studentNo}/friends")
//    public List<StudentView> getFriends(@PathVariable String studentNo) {
//        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        return s.getFriends().stream().map(StudentView::new).collect(Collectors.toList());
//    }
//
//
//    @GetMapping("/{studentNo}/blocked")
//    public List<StudentView> getBlockedFriends(@PathVariable String studentNo) {
//        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        return s.getBlocked().stream().map(StudentView::new).collect(Collectors.toList());
//    }
//
//    @GetMapping("/{studentNo}/pending")
//    public List<StudentView> getPendingFriends(@PathVariable String studentNo) {
//        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        return s.getPending().stream().map(StudentView::new).collect(Collectors.toList());
//    }
//
//    @GetMapping("/{studentNo}/requested")
//    public List<StudentView> getRequestedFriends(@PathVariable String studentNo) {
//        Student s = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        return s.getRequested().stream().map(StudentView::new).collect(Collectors.toList());
//    }
//
//
//    @PutMapping("/{studentNo}/accept")
//    public void acceptFriendship(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//        Student friend = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));
//
//        try {
//            student.acceptRequest(friend);
//            friend.addFriend(student);
//        }catch (Exception exception) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        }
//
//        studentRepository.save(student);
//        studentRepository.save(friend);
//    }
//
//    @PutMapping("/{studentNo}/block")
//    public void blockFriend(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//        Student friend = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));
//
//        try {
//            studentRepository.save(student.blockFriend(friend));
//        }catch (Exception exception) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        }
//    }
//
//    @PutMapping("/{studentNo}/unblock")
//    public void unblockFriend(@PathVariable String studentNo, @RequestBody String friendStudentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//        Student friend = studentRepository.findByStudentNumber(new StudentNumber(friendStudentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + friendStudentNo + " not found"));
//
//        try {
//            studentRepository.save(student.unblockFriend(friend));
//        }catch (Exception exception) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        }
//    }
//
//    @GetMapping("/{studentNo}/enrollmentLists")
//    public List<EnrollmentListView> getFriendsEnrollmentLists(@PathVariable String studentNo) {
//        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo))
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested Student with id " + studentNo + " not found"));
//
//        List<Student> friends = student.getFriendsWhoDoesntBlock();
//        List<EnrollmentList> lists = new ArrayList<>();
//        for (Student s : friends)
//            lists.addAll(enrollmentListRepository.findByOwner(s));
//
//        return lists.stream().map(EnrollmentListView::new).collect(Collectors.toList());
//    }
//
//
//}
