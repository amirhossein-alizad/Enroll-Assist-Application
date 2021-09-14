package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.user.FriendshipController;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import ir.proprog.enrollassist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendshipController.class)
public class FriendshipControllerTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @Autowired
    private MockMvc mvc;

    User user1, user2, student1, student2;

    @BeforeEach
    void SetUp() {
        user1 = new User("Ali", "010101");
        user2 = new User("Zahra", "111111");
        given(userRepository.findByUserId("010101")).willReturn(java.util.Optional.of(user1));
        given(userRepository.findByUserId("111111")).willReturn(java.util.Optional.of(user2));
        student1 = mock(User.class);
        student2 = mock(User.class);
        given(userRepository.findByUserId("123")).willReturn(Optional.of(student1));
        given(userRepository.findByUserId("456")).willReturn(Optional.of(student2));
    }

        @Test
    public void Friendship_requests_are_sent_correctly() throws Exception{
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Invalid_friendship_requests_are_returned_with_correct_errors() throws Exception{
        user1.getFriends().add(user2);
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "This user is already your friend."));
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_requested_student_does_not_exist() throws Exception{
        mvc.perform(put("/friends/010101")
                .content("2222")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 2222 not found"));
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_sender_does_not_exist() throws Exception {
        mvc.perform(put("/friends/333")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 333 not found"));
    }

    @Test
    public void Friendships_are_removed_correctly() throws Exception {
        user1.addFriend(user2);
        user2.addFriend(user1);
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_student_is_not_a_friend() throws Exception {
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "There is no relation between these users."));
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_student_does_not_exist() throws Exception {
        mvc.perform(delete("/friends/3333")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 3333 not found"));
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_friend_does_not_exist() throws Exception {
        mvc.perform(delete("/friends/010101")
                .content("2222")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 2222 not found"));
    }

    @Test
    public void Student_friends_in_different_states_are_returned_correctly() throws Exception {
        User friend1 = new User("jack", "111111");
        User friend2 = new User("john", "222222");
        User friend3 = new User("rob", "333333");
        User friend4 = new User("fin", "444444");
        User req = new User("matt", "877877");
        User pending = new User("mark", "123456");
        User blocked = new User("jake", "987654");
        user1.getFriends().add(friend1);
        user1.getFriends().add(friend2);
        user1.getFriends().add(friend3);
        user1.getFriends().add(friend4);
        user1.getBlocked().add(blocked);
        user1.getRequested().add(req);
        user1.getPending().add(pending);
        mvc.perform(get("/friends/010101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    public void Friendships_are_not_found_if_student_does_not_exist() throws Exception {
        mvc.perform(get("/friends/3333")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 3333 not found"));
    }

    @Test
    public void Students_friends_are_returned_correctly() throws Exception {
        User friend1 = new User("jack", "111111");
        User friend2 = new User("john", "222222");
        User friend3 = new User("rob", "333333");
        user1.getFriends().add(friend1);
        user1.getFriends().add(friend2);
        user1.getFriends().add(friend3);
        mvc.perform(get("/friends/010101/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void Friends_are_not_found_if_student_does_not_exist() throws Exception {
        mvc.perform(get("/friends/3333/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested user with id 3333 not found"));
    }

    @Test
    public void Blocked_students_of_one_student_are_returned_correctly() throws Exception{
        when(student1.getBlocked()).thenReturn(List.of(user1, user2));
        mvc.perform(get("/friends/123/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].userId", is(user1.getUserId())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].userId", is(user2.getUserId())));
    }


    @Test
    public void Student_with_no_blocked_friends_returns_empty_list() throws Exception{
        mvc.perform(get("/friends/123/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Unreal_Student_cant_return_its_blocked_friends() throws Exception{
        mvc.perform(get("/friends/567/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Pending_students_of_one_student_are_returned_correctly() throws Exception{
        when(student1.getPending()).thenReturn(List.of(user1, user2));
        mvc.perform(get("/friends/123/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].userId", is(user1.getUserId())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].userId", is(user2.getUserId())));
    }

    @Test
    public void Student_with_no_pending_friends_returns_empty_list() throws Exception{
        mvc.perform(get("/friends/123/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Requested_student_of_one_student_is_returned_correctly() throws Exception{
        User requestedStudent = new User("reza", "010104");
        when(student1.getRequested()).thenReturn(List.of(requestedStudent));
        mvc.perform(get("/friends/123/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(requestedStudent.getUserId())))
                .andExpect(jsonPath("$[0].name", is(requestedStudent.getName())));
    }

    @Test
    public void Student_with_no_requested_friends_returns_empty_list() throws Exception{
        mvc.perform(get("/friends/123/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Student_can_block_other_student() throws Exception{
        when(student1.blockFriend(student2)).thenReturn(student1);
        mvc.perform(put("/friends/123/block")
                .content("456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void Student_cant_block_other_student_who_is_not_her_friend() throws Exception{
        when(student1.blockFriend(student2)).thenThrow(new Exception("This student is not your friend."));
        mvc.perform(put("/friends/123/block")
                .content("456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Student_can_unblock_the_blocked_student() throws Exception{
        when(student1.unblockFriend(student2)).thenReturn(student1);
        mvc.perform(put("/friends/123/unblock")
                .content("456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Student_cant_unblock_the_student_who_is_not_blocked() throws Exception{
        when(student1.unblockFriend(student2)).thenThrow(new Exception("This user is not blocked."));
        mvc.perform(put("/friends/123/unblock")
                .content("456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    public void clean(){
        this.user1.getFriends().clear();
        this.user1.getBlocked().clear();
        this.user1.getRequested().clear();
        this.user1.getPending().clear();
        this.user2.getFriends().clear();
        this.user2.getBlocked().clear();
        this.user2.getRequested().clear();
        this.user2.getPending().clear();
    }

    @Test
    public void Student_can_see_her_friends_enrollments() throws Exception{
        User user = mock(User.class);
        Student student = new Student("000012", "Undergraduate");
        User friend = new User("Nina", "NN");
        friend.addStudent(student);
        EnrollmentList enrollmentList1 = new EnrollmentList("Sarina's list", student);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        when(user.getFriendsWhoDoesntBlock()).thenReturn(List.of(friend));
        given(enrollmentListRepository.findByOwner(student)).willReturn(List.of(enrollmentList1));
        mvc.perform(get("/friends/1/lists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].enrollmentListName", is(enrollmentList1.getListName())));
    }
}
