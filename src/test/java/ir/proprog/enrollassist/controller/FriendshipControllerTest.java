package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.friendship.FriendshipController;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.domain.StudentNumber;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
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
    private StudentRepository studentRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @Autowired
    private MockMvc mvc;

    @Test
    public void Friendship_requests_are_sent_correctly() throws Exception{
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Invalid_friendship_requests_are_returned_with_correct_errors() throws Exception{
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        student1.getFriends().add(student2);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "This user is already your friend."));
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_requested_student_does_not_exist() throws Exception{
        Student student1 = new Student("010101", "bob");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 111111 not found"));
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_sender_does_not_exist() throws Exception {
        mvc.perform(put("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Friendships_are_removed_correctly() throws Exception {
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        student1.addFriend(student2);
        student2.addFriend(student1);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_student_is_not_a_friend() throws Exception {
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "There is no relation between these students."));
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_student_does_not_exist() throws Exception {
        Student student = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student));
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_friend_does_not_exist() throws Exception {
        Student student = new Student("010101", "bob");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));
        mvc.perform(delete("/friends/010101")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 111111 not found"));
    }

    @Test
    public void Student_friends_in_different_states_are_returned_correctly() throws Exception {
        Student student = new Student("010101", "bob");
        Student friend1 = new Student("111111", "jack");
        Student friend2 = new Student("222222", "john");
        Student friend3 = new Student("333333", "rob");
        Student friend4 = new Student("444444", "fin");
        Student req = new Student("877877", "matt");
        Student pending = new Student("123456", "mark");
        Student blocked = new Student("987654", "jake");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));
        student.getFriends().add(friend1);
        student.getFriends().add(friend2);
        student.getFriends().add(friend3);
        student.getFriends().add(friend4);
        student.getBlocked().add(blocked);
        student.getRequested().add(req);
        student.getPending().add(pending);
        mvc.perform(get("/friends/010101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    public void Friendships_are_not_found_if_student_does_not_exist() throws Exception {
        mvc.perform(get("/friends/010101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Students_friends_are_returned_correctly() throws Exception {
        Student student = new Student("010101", "bob");
        Student friend1 = new Student("111111", "jack");
        Student friend2 = new Student("222222", "john");
        Student friend3 = new Student("333333", "rob");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));
        student.getFriends().add(friend1);
        student.getFriends().add(friend2);
        student.getFriends().add(friend3);
        mvc.perform(get("/friends/010101/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void Friends_are_not_found_if_student_does_not_exist() throws Exception {
        mvc.perform(get("/friends/010101/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Blocked_students_of_one_student_are_returned_correctly() throws Exception{
        Student student = mock(Student.class);
        Student blockedStudent1 = new Student("010102", "reza");
        Student blockedStudent2 = new Student("010103", "sara");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getBlocked()).thenReturn(List.of(blockedStudent1, blockedStudent2));
        mvc.perform(get("/friends/010101/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")))
                .andExpect(jsonPath("$[1].studentNo", is("010103")))
                .andExpect(jsonPath("$[1].name", is("sara")));
    }


    @Test
    public void Student_with_no_blocked_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/friends/010101/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Unreal_Student_cant_return_its_blocked_friends() throws Exception{
        mvc.perform(get("/friends/010101/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Pending_students_of_one_student_are_returned_correctly() throws Exception{
        Student student = mock(Student.class);
        Student pendingStudent1 = new Student("010102", "reza");
        Student pendingStudent2 = new Student("010103", "sara");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getPending()).thenReturn(List.of(pendingStudent1, pendingStudent2));
        mvc.perform(get("/friends/010101/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")))
                .andExpect(jsonPath("$[1].studentNo", is("010103")))
                .andExpect(jsonPath("$[1].name", is("sara")));
    }

    @Test
    public void Student_with_no_pending_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/friends/010101/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Requested_student_of_one_student_is_returned_correctly() throws Exception{
        Student student = mock(Student.class);
        Student requestedStudent = new Student("010102", "reza");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getRequested()).thenReturn(List.of(requestedStudent));
        mvc.perform(get("/friends/010101/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")));
    }

    @Test
    public void Student_with_no_requested_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/friends/010101/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Student_cannot_add_other_student_to_his_friends_list_without_request() throws Exception{
        Student student1 = new Student("010101", "reza");
        Student student2 = new Student("010102", "ali");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        mvc.perform(put("/friends/010101/accept")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "This user did not request to be your friend."));
    }

    @Test
    public void Student_can_block_other_student() throws Exception{
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        when(student1.blockFriend(student2)).thenReturn(student1);
        mvc.perform(put("/friends/010101/block")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void Student_cant_block_other_student_who_is_not_her_friend() throws Exception{
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        when(student1.blockFriend(student2)).thenThrow(new Exception("This student is not your friend."));
        mvc.perform(put("/friends/010101/block")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Student_can_unblock_the_blocked_student() throws Exception{
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        when(student1.unblockFriend(student2)).thenReturn(student1);
        mvc.perform(put("/friends/010101/unblock")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Student_cant_unblock_the_student_who_is_not_blocked() throws Exception{
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        when(student1.unblockFriend(student2)).thenThrow(new Exception("This user is not blocked."));
        mvc.perform(put("/friends/010101/unblock")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Student_can_see_her_friends_enrollments() throws Exception{
        Student student = mock(Student.class);
        Student friend1 = new Student("000012", "Sarina");
        EnrollmentList enrollmentList1 = new EnrollmentList("Sarina's list", friend1);
        given(studentRepository.findByStudentNumber(new StudentNumber("000011"))).willReturn(Optional.of(student));
        when(student.getFriendsWhoDoesntBlock()).thenReturn(List.of(friend1));
        given(enrollmentListRepository.findByOwner(friend1)).willReturn(List.of(enrollmentList1));
        mvc.perform(get("/friends/000011/enrollmentLists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].enrollmentListName", is(enrollmentList1.getListName())));
    }
}
