package ir.proprog.enrollassist.controller.friendship;

import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendshipController {
    private UserRepository userRepository;

    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "requested user with id " + userId + " not found"));
    }

    @PutMapping("/{userId}")
    public void sendFriendshipRequest(@PathVariable String userId, @RequestBody String friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        try {
            user.sendFriendshipRequest(friend);
            friend.receiveFriendshipRequest(user);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        userRepository.save(user);
        userRepository.save(friend);
    }



    @DeleteMapping("/{userId}")
    public void removeFriendship(@PathVariable String userId, @RequestBody String friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        try {
            user.removeFriend(friend);
            friend.removeFriend(user);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        userRepository.save(user);
        userRepository.save(friend);
    }

    @GetMapping("/{userId}")
    public List<UserView> getAllFriends(@PathVariable String userId) {
        User user = getUser(userId);
        return user.getAllFriends().stream().map(UserView::new).collect(Collectors.toList());
    }

    @GetMapping("/{userId}/friends")
    public List<UserView> getFriends(@PathVariable String userId) {
        User s = getUser(userId);
        return s.getFriends().stream().map(UserView::new).collect(Collectors.toList());
    }


    @GetMapping("/{userId}/blocked")
    public List<UserView> getBlockedFriends(@PathVariable String userId) {
        User s = getUser(userId);
        return s.getBlocked().stream().map(UserView::new).collect(Collectors.toList());
    }

    @GetMapping("/{userId}/pending")
    public List<UserView> getPendingFriends(@PathVariable String userId) {
        User s = getUser(userId);
        return s.getPending().stream().map(UserView::new).collect(Collectors.toList());
    }

    @GetMapping("/{userId}/requested")
    public List<UserView> getRequestedFriends(@PathVariable String userId) {
        User s = getUser(userId);
        return s.getRequested().stream().map(UserView::new).collect(Collectors.toList());
    }


    @PutMapping("/{userId}/accept")
    public void acceptFriendship(@PathVariable String userId, @RequestBody String friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        try {
            user.acceptRequest(friend);
            friend.addFriend(user);
        }catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        userRepository.save(user);
        userRepository.save(friend);
    }

    @PutMapping("/{userId}/block")
    public void blockFriend(@PathVariable String userId, @RequestBody String friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        try {
            userRepository.save(user.blockFriend(friend));
        }catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PutMapping("/{userId}/unblock")
    public void unblockFriend(@PathVariable String userId, @RequestBody String friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        try {
            userRepository.save(user.unblockFriend(friend));
        }catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

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
}
