package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    User user, bebe;
    Student student1, student2;
    TestStudentBuilder testStudentBuilder;
    @BeforeEach
    void setUp() throws Exception{
        user = new User("user", "12");
        testStudentBuilder = new TestStudentBuilder();
        student1 = testStudentBuilder.build();
        user.addStudent(student1);
        bebe = new User("bebe", "22");
    }

    @Test
    void User_name_can_not_be_empty(){
        Throwable error = assertThrows(Exception.class, () ->
                new User("", "12"));
        assertEquals(error.getMessage().toString(), "Name can not be empty.");
    }

    @Test
    void Students_with_the_same_name_are_added_to_users_list_correctly() throws Exception{
        student2 = testStudentBuilder
                .withStudentNumber("810197546")
                .build();
        user.addStudent(student2);
        assertThat(user.getStudents())
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(student1, student2);
    }

    @Test
    void Student_can_block_her_friend() throws Exception {
        User friend = new User("Ali", "30");
        bebe.addFriend(friend);
        bebe.blockFriend(friend);
        assertThat(bebe.getBlocked()).hasSize(1);
    }


    @Test
    void Student_cant_block_other_student_who_is_not_her_friend() throws ExceptionList {
        User friend = new User("Ali", "30");
        Throwable error = assertThrows(Exception.class, () -> bebe.blockFriend(friend));
        assertEquals(error.getMessage(), "This student is not your friend.");
    }

    @Test
    void Student_can_unblock_the_blocked_student_correctly() throws Exception {
        User friend = new User("Ali", "30");
        bebe.getBlocked().add(friend);
        bebe.unblockFriend(friend);
        assertThat(bebe.getBlocked()).isEmpty();
    }

    @Test
    void Student_cant_unblocked_the_student_who_is_not_blocked() throws ExceptionList {
        User friend = new User("Ali", "30");
        Throwable error = assertThrows(Exception.class, () -> bebe.unblockFriend(friend));
        assertEquals(error.getMessage(), "This user is not blocked.");
    }

    @Test
    void Students_friends_who_dont_block_student_return_correctly() throws ExceptionList {
        User friend1 = new User("Ali", "30");
        User friend2 = new User("Zahra", "31");
        bebe.getFriends().add(friend1);
        bebe.getFriends().add(friend2);
        friend1.getFriends().add(bebe);
        friend2.getBlocked().add(bebe);
        assertThat(bebe.getFriendsWhoDoesntBlock()).hasSize(1).contains(friend1);
    }
    @Test
    void Students_cannot_send_friendship_request_to_their_friends() throws ExceptionList {
        User friend = new User("Ali", "30");
        bebe.getFriends().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "This user is already your friend.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_students_they_have_already_requested() throws ExceptionList {
        User friend = new User("Ali", "30");
        bebe.getRequested().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You requested to this user before.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_pending_students() throws ExceptionList {
        User friend = new User("Ali", "30");
        bebe.getPending().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "This user requested first.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_blocked_students() throws ExceptionList {
        User friend = new User("Ali", "30");
        bebe.getBlocked().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You have blocked this user.");
    }

    @Test
    void Student_can_send_friendship_request_to_others_if_they_are_not_found_in_any_of_her_lists() throws Exception {
        User friend1 = new User("Ali", "30");
        User friend2 = new User("Zahra", "31");
        User friend3 = new User("Arash", "33");
        User friend4 = new User("Amir", "32");

        bebe.getFriends().add(friend1);
        bebe.getFriends().add(friend2);
        bebe.getRequested().add(friend3);

        bebe.sendFriendshipRequest(friend4);

        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_cannot_send_request_to_herself() {
        User friend = new User("bebe", "22");
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You cannot send friendship request to yourself.");
    }

    @Test
    void Students_cannot_receive_friendship_request_if_they_have_blocked_the_other_student() {
        User friend = new User("Ali", "30");
        friend.getBlocked().add(bebe);
        Throwable error = assertThrows(Exception.class, () -> friend.receiveFriendshipRequest(bebe));
        assertEquals(error.getMessage(), "You have been blocked by this user.");
    }

    @Test
    void Friendships_are_removed_correctly() throws Exception {
        User friend1 = new User("Ali", "30");
        User friend2 = new User("Zahra", "31");
        User friend3 = new User("Arash", "33");
        User friend4 = new User("Amir", "32");

        bebe.getFriends().add(friend1);
        bebe.getRequested().add(friend2);
        bebe.getBlocked().add(friend3);
        bebe.getPending().add(friend4);
        bebe.removeFriend(friend1);
        bebe.removeFriend(friend2);
        bebe.removeFriend(friend3);
        assertThat(bebe.getAllFriends()).hasSize(1);
    }

    @Test
    void Friendships_cannot_be_removed_without_any_relation() throws ExceptionList {
        User friend = new User("Ali", "30");
        Throwable error = assertThrows(Exception.class, () -> bebe.removeFriend(friend));
        assertEquals(error.getMessage(), "There is no relation between these users.");
    }

    @Test
    void All_friends_are_returned_correctly() throws ExceptionList {
        User friend1 = new User("Ali", "30");
        User friend2 = new User("Zahra", "31");
        User friend3 = new User("Arash", "33");
        User friend4 = new User("Amir", "32");
        bebe.getFriends().add(friend1);
        bebe.getRequested().add(friend2);
        bebe.getBlocked().add(friend3);
        bebe.getPending().add(friend4);
        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_are_accepted_correctly() throws Exception {
        User friend = new User("Ali", "30");
        bebe.getRequested().add(friend);
        bebe.acceptRequest(friend);
        assertThat(bebe.getFriends()).hasSize(1);
    }

    @Test
    void Student_cannot_accept_friends_who_are_not_in_her_requested_list() throws ExceptionList {
        User friend = new User("Ali", "30");
        bebe.getBlocked().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.acceptRequest(friend));
        assertEquals(error.getMessage(), "This user did not request to be your friend.");
    }

}
