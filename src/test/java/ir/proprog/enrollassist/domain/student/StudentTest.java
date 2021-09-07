package ir.proprog.enrollassist.domain.student;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.program.MajorProgram;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StudentTest {
    private Student bebe;
    private Course math1, phys1, prog, economy, maaref, andishe, math2;
    Section math1_1, prog1_1, andishe1_1, math2_2, math2_1, math1_2;

    @BeforeEach
    void setUp() throws Exception{
        math1 = new Course("4444444", "MATH1", 3, "Undergraduate");
        phys1 = new Course("8888888", "PHYS1", 3, "Undergraduate");
        prog = new Course("7777777", "PROG", 4, "Undergraduate");
        economy = new Course("1111111", "ECO", 3, "Undergraduate");
        maaref = new Course("5555555", "MAAREF", 2, "Undergraduate");
        andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        math2 = new Course("2222222", "MATH2", 3, "Undergraduate").withPre(math1);
        Major major = new Major("123", "CE");
        Program p = new MajorProgram(major, "Undergraduate", 140, 140);
        p.addCourse(math1, phys1, prog, economy, maaref, andishe, math2);
        bebe = new TestStudentBuilder()
                .withGraduateLevel("Undergraduate")
                .build()
                .addProgram(p);
        math1_1 = new Section(math1, "01");
        math1_2 = new Section(math1, "02");
        prog1_1 = new Section(prog, "01");
        andishe1_1 = new Section(andishe, "01");
        math2_1 = new Section(math2, "01");
        math2_2 = new Section(math2, "02");
    }

    @Test
    void Student_with_multiple_invalid_fields_cannot_be_created() {
        Throwable error = assertThrows(
                ExceptionList.class, () -> {
                    Student bebe = new TestStudentBuilder()
                            .withName("")
                            .withStudentNumber("")
                            .withGraduateLevel("student")
                            .build();
                }
        );
        assertEquals(error.toString(), "{\"1\":\"Student number can not be empty.\"," +
                "\"2\":\"Student name can not be empty.\"," +
                "\"3\":\"Graduate level is not valid.\"}");
    }

    @Test
    void Student_with_valid_data_can_be_created() {
        String error = "";
        try {
            Student bebe = new TestStudentBuilder().build();
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "");
    }

    @Test
    void Course_with_grade_less_than_ten_is_not_passed() throws Exception {
        bebe.setGrade("13981", math1, 9.99);
        assertThat(bebe.hasPassed(math1))
                .isFalse();
    }

    @Test
    void Course_with_grade_less_than_ten_is_not_passed_but_calculated_in_GPA() throws Exception {
        bebe.setGrade("13981", math1, 15.5);
        bebe.setGrade("13981", phys1, 9);
        bebe.setGrade("13981", prog, 17.25);
        bebe.setGrade("13981", economy, 19.5);
        bebe.setGrade("13981", maaref, 16);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(15.53);
    }

    @Test
    void Student_has_not_passed_records_that_are_not_in_grades_set() {
        Student student = new TestStudentBuilder().buildMock();
        assertThat(student.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Student_has_passed_records_that_are_in_grades_set()  throws Exception {
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void PHD_student_has_not_passed_course_when_her_grade_is_less_than_14() throws Exception {
        Student bebe = new TestStudentBuilder().build();
        Course math1 = new Course("1111111", "MATH1", 3, "PHD");
        bebe.setGrade("13981", math1, 13.5);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Undergraduate_student_has_passed_masters_course_br_grade_less_than_12_and_more_than_10() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Masters");
        bebe.setGrade("13981", math1, 11);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly() throws Exception {
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly()  throws Exception {
        bebe.setGrade("13981", math1, 19);
        bebe.setGrade("13981", prog, 17);
        bebe.setGrade("13981", andishe, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(18.11);
    }

    @Test
    void Student_returns_courses_with_passed_prerequisites_as_takeable_correctly() throws Exception {
        bebe.setGrade("13981", math1, 20);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(6)
                .containsExactlyInAnyOrder(prog, andishe, math2, economy, maaref, phys1);
    }

    @Test
    void Student_does_not_return_courses_which_prerequisites_are_not_passed_as_takeable_correctly() {
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(6)
                .containsExactlyInAnyOrder(prog, andishe, math1, economy, maaref, phys1);
    }

    @Test
    void Student_returns_takeable_sections_which_courses_have_no_prerequisites_correctly() {
        assertThat(bebe.getTakeableSections(List.of(math1_1, math1_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math1_1, math1_2, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_their_courses_are_passed_correctly() throws Exception {
        bebe.setGrade("13981", math1, 20);
        assertThat(bebe.getTakeableSections(List.of(math1_1, math1_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(prog1_1, andishe1_1);
    }

    @Test
    void Student_returns_sections_which_prerequisites_for_their_courses_have_been_passed_correctly() throws Exception {
        bebe.setGrade("13981", math1, 20);
        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math2_2, math2_1, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_prerequisites_for_their_courses_have_not_been_passed_correctly() {
        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(math1_1, prog1_1, andishe1_1);
    }


    @Test
    void Grade_of_course_with_valid_term_can_set_correctly() throws ExceptionList {
        bebe.setGrade("13962", math1, 19.1);
        assertThat(bebe.getGrades()).hasSize(1);
    }

    @Test
    void Grade_of_course_with_invalid_season_throws_error() {
        Throwable error = assertThrows(ExceptionList.class, () -> bebe.setGrade("13960", math1, 19.1));
        assertEquals(error.toString(), "{\"1\":\"Season of term is not valid.\"}");
    }

    @Test
    void Student_can_block_her_friend() throws Exception {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.addFriend(friend);
        bebe.blockFriend(friend);
        assertThat(bebe.getBlocked()).hasSize(1);
    }


    @Test
    void Student_cant_block_other_student_who_is_not_her_friend() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("123960").build();
        Throwable error = assertThrows(Exception.class, () -> bebe.blockFriend(friend));
        assertEquals(error.getMessage(), "This student is not your friend.");
    }

    @Test
    void Student_can_unblock_the_blocked_student_correctly() throws Exception {
        Student friend = new TestStudentBuilder().withStudentNumber("123960").build();
        bebe.getBlocked().add(friend);
        bebe.unblockFriend(friend);
        assertThat(bebe.getBlocked()).isEmpty();
    }

    @Test
    void Student_cant_unblocked_the_student_who_is_not_blocked() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("123960").build();
        Throwable error = assertThrows(Exception.class, () -> bebe.unblockFriend(friend));
        assertEquals(error.getMessage(), "This user is not blocked.");
    }

    @Test
    void Students_friends_who_dont_block_student_return_correctly() throws ExceptionList {
        Student friend1 = new TestStudentBuilder().withStudentNumber("123960").build();
        Student friend2 = new TestStudentBuilder().withStudentNumber("123961").build();
        bebe.getFriends().add(friend1);
        bebe.getFriends().add(friend2);
        friend1.getFriends().add(bebe);
        friend2.getBlocked().add(bebe);
        assertThat(bebe.getFriendsWhoDoesntBlock()).hasSize(1).contains(friend1);
    }
    @Test
    void Students_cannot_send_friendship_request_to_their_friends() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getFriends().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "This user is already your friend.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_students_they_have_already_requested() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getRequested().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You requested to this user before.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_pending_students() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getPending().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "This user requested first.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_blocked_students() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getBlocked().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You have blocked this user.");
    }

    @Test
    void Student_can_send_friendship_request_to_others_if_they_are_not_found_in_any_of_her_lists() throws Exception {
        Student friend1 = new TestStudentBuilder().withStudentNumber("810197001").build();
        Student friend2 = new TestStudentBuilder().withStudentNumber("810197002").build();
        Student friend3 = new TestStudentBuilder().withStudentNumber("810197003").build();
        Student friend4 = new TestStudentBuilder().withStudentNumber("810197004").build();

        bebe.getFriends().add(friend1);
        bebe.getFriends().add(friend2);
        bebe.getRequested().add(friend3);

        bebe.sendFriendshipRequest(friend4);

        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_cannot_send_request_to_herself() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810190000").build();
        Throwable error = assertThrows(Exception.class, () -> bebe.sendFriendshipRequest(friend));
        assertEquals(error.getMessage(), "You cannot send friendship request to yourself.");
    }

    @Test
    void Students_cannot_receive_friendship_request_if_they_have_blocked_the_other_student() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        friend.getBlocked().add(bebe);
        Throwable error = assertThrows(Exception.class, () -> friend.receiveFriendshipRequest(bebe));
        assertEquals(error.getMessage(), "You have been blocked by this user.");
    }

    @Test
    void Friendships_are_removed_correctly() throws Exception {
        Student friend1 = new TestStudentBuilder().withStudentNumber("810197001").build();
        Student friend2 = new TestStudentBuilder().withStudentNumber("810197002").build();
        Student friend3 = new TestStudentBuilder().withStudentNumber("810197003").build();
        Student friend4 = new TestStudentBuilder().withStudentNumber("810197004").build();
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
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        Throwable error = assertThrows(Exception.class, () -> bebe.removeFriend(friend));
        assertEquals(error.getMessage(), "There is no relation between these students.");
    }

    @Test
    void All_friends_are_returned_correctly() throws ExceptionList {
        Student friend1 = new TestStudentBuilder().withStudentNumber("810197001").build();
        Student friend2 = new TestStudentBuilder().withStudentNumber("810197002").build();
        Student friend3 = new TestStudentBuilder().withStudentNumber("810197003").build();
        Student friend4 = new TestStudentBuilder().withStudentNumber("810197004").build();
        bebe.getFriends().add(friend1);
        bebe.getRequested().add(friend2);
        bebe.getBlocked().add(friend3);
        bebe.getPending().add(friend4);
        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_are_accepted_correctly() throws Exception {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getRequested().add(friend);
        bebe.acceptRequest(friend);
        assertThat(bebe.getFriends()).hasSize(1);
    }

    @Test
    void Student_cannot_accept_friends_who_are_not_in_her_requested_list() throws ExceptionList {
        Student friend = new TestStudentBuilder().withStudentNumber("810197001").build();
        bebe.getBlocked().add(friend);
        Throwable error = assertThrows(Exception.class, () -> bebe.acceptRequest(friend));
        assertEquals(error.getMessage(), "This user did not request to be your friend.");
    }

}
