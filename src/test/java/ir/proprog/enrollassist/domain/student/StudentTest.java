package ir.proprog.enrollassist.domain.student;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.Section;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StudentTest {

    @Test
    void Student_with_invalid_data_cant_be_created() {
        String error = "";
        try {
            Major major = mock(Major.class);
            Student bebe = new Student("", "", major, "student");
        }catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Student number can not be empty.\"," +
                "\"2\":\"Student name can not be empty.\"," +
                "\"3\":\"Education grade is not valid.\"}");
    }

    @Test
    void Student_with_valid_data_cant_be_created() {
        String error = "";
        try {
            Major major = mock(Major.class);
            Student bebe = new Student("1234", "Mehrnaz", major, "PHD");
        }catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "");
    }

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed() throws Exception {
        Major major = mock(Major.class);
        Student bebe = new Student("810197546", "bebe", major, "Undergraduate");
        Course math1 = new Course("4444444", "MATH1", 3, "Undergraduate");
        bebe.setGrade("13981", math1, 9.99);
        assertThat(bebe.hasPassed(math1))
                .isFalse();
    }

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed_But_Calculated_in_GPA() throws Exception {
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4444444", "MATH1", 3, "Undergraduate");
        Course phys1 = new Course("8888888", "PHYS1", 3, "Undergraduate");
        Course prog = new Course("7777777", "PROG", 4, "Undergraduate");
        Course economy = new Course("1111111", "ECO", 3, "Undergraduate");
        Course maaref = new Course("5555555", "MAAREF", 2, "Undergraduate");
        bebe.setGrade("13981", math1, 15.5);
        bebe.setGrade("13981", phys1, 9);
        bebe.setGrade("13981", prog, 17.25);
        bebe.setGrade("13981", economy, 19.5);
        bebe.setGrade("13981", maaref, 16);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(15.53);
    }

    @Test
    void Student_has_not_passed_records_that_are_not_in_grades_set()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Student_has_passed_records_that_are_in_grades_set()  throws Exception {
        Major major = mock(Major.class);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void PHD_student_has_not_passed_course_when_her_grade_is_less_than_14()  throws Exception {
        Major major = mock(Major.class);
        Student bebe = new Student("810197000", "bebe", major, "PHD");
        Course math1 = new Course("1111111", "MATH1", 3, "PHD");
        bebe.setGrade("13981", math1, 13.5);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Undergraduate_student_has_passed_masters_course_br_grade_less_than_12_and_more_than_10()  throws Exception {
        Major major = mock(Major.class);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        Course math1 = new Course("1111111", "MATH1", 3, "Masters");
        bebe.setGrade("13981", math1, 11);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        bebe.setGrade("13981", math1, 19);
        bebe.setGrade("13981", prog, 17);
        bebe.setGrade("13981", andishe, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(18.25);
    }

    @Test
    void Student_returns_takeable_courses_with_no_prerequisites_correctly() throws Exception{
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(math1, prog, andishe);
    }


    @Test
    void Student_does_not_return_passed_courses_as_takeable() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        bebe.setGrade("13981", math1, 20);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(prog, andishe);
    }

    @Test
    void Student_returns_courses_with_passed_prerequisites_as_takeable_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Course math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        bebe.setGrade("13981", math1, 20);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(prog, andishe, math2);
    }

    @Test
    void Student_does_not_return_courses_which_prerequisites_are_not_passed_as_takeable_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Course math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");

        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(prog, andishe, math1);
    }

    @Test
    void Student_returns_takeable_sections_which_courses_have_no_prerequisites_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Section math1_1 = new Section(math1, "01");
        Section math1_2 = new Section(math1, "02");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");

        assertThat(bebe.getTakeableSections(List.of(math1_1, math1_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math1_1, math1_2, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_their_courses_are_passed_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        bebe.setGrade("13981", math1, 20);

        Section math1_1 = new Section(math1, "01");
        Section math1_2 = new Section(math1, "02");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        assertThat(bebe.getTakeableSections(List.of(math1_1, math1_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(prog1_1, andishe1_1);
    }

    @Test
    void Student_returns_sections_which_prerequisites_for_their_courses_have_been_passed_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Course math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");
        bebe.setGrade("13981", math1, 20);

        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math2_2, math2_1, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_prerequisites_for_their_courses_have_not_been_passed_correctly() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "Undergraduate");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Undergraduate");
        Course math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");

        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(math1_1, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_are_not_for_her_education_grade() throws Exception {
        Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        Course prog = new Course("2222222", "PROG", 3, "PHD");
        Course andishe = new Course("3333333", "ANDISHE", 2, "Masters");
        Section math1_1 = new Section(math1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        Student bebe = new Student("810197000", "bebe", major, "Undergraduate");

        assertThat(bebe.getTakeableSections(List.of(math1_1, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(1)
                .containsExactlyInAnyOrder(math1_1);
    }


    @Test
    void Grade_of_course_with_valid_term_can_set_correctly() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        try {
            Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
            bebe.setGrade("13962", math1, 19.1);
        }catch (ExceptionList e) {
            error = e.toString();
        }
        assertEquals(error, "");
    }

    @Test
    void Grade_of_course_with_invalid_season_cant_set_correctly() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        try {
            Course math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
            bebe.setGrade("13960", math1, 19.1);
        }catch (ExceptionList e) {
            error = e.toString();
        }
        assertEquals(error, "{\"1\":\"Season of term is not valid.\"}");
    }

    @Test
    void Student_can_block_her_friend() {
        Student student = new Student("143960", "sara");
        Student friend = new Student("123960", "sarina");
        student.addFriend(friend);
        String error = "";
        try {
            student.blockFriend(friend);
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "");
    }


    @Test
    void Student_cant_block_other_student_who_is_not_her_friend() {
        Student student = new Student("143960", "sara");
        Student friend = new Student("123960", "sarina");
        String error = "";
        try {
            student.blockFriend(friend);
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "This student is not your friend.");
    }

    @Test
    void Student_can_unblock_the_blocked_student() {
        Student student = new Student("143960", "sara");
        Student friend = new Student("123960", "sarina");
        student.getBlocked().add(friend);
        String error = "";
        try {
            student.unblockFriend(friend);
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "");
    }

    @Test
    void Student_cant_unblocked_the_student_who_is_not_blocked() {
        Student student = new Student("143960", "sara");
        Student friend = new Student("123960", "sarina");
        String error = "";
        try {
            student.unblockFriend(friend);
        } catch (Exception exception) {
            error = exception.getMessage();
        }
        assertEquals(error, "This user is not blocked.");
    }

    @Test
    void Students_friends_who_dont_block_student_return_correctly() {
        Student student = new Student("143960", "sara");
        Student friend1 = new Student("143961", "sarina");
        Student friend2 = new Student("143962", "saber");
        student.getFriends().add(friend1);
        student.getFriends().add(friend2);
        friend1.getFriends().add(student);
        friend2.getBlocked().add(student);
        List<Student> friends = student.getFriendsWhoDoesntBlock();
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), friend1);
    }
    @Test
    void Students_cannot_send_friendship_request_to_their_friends() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getFriends().add(friend);

        try {
            bebe.sendFriendshipRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "This user is already your friend.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_students_they_have_already_requested() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getRequested().add(friend);

        try {
            bebe.sendFriendshipRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "You requested to this user before.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_pending_students() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getPending().add(friend);

        try {
            bebe.sendFriendshipRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "This user requested first.");
    }

    @Test
    void Students_cannot_send_friendship_request_to_blocked_students() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getBlocked().add(friend);

        try {
            bebe.sendFriendshipRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "You have blocked this user.");
    }

    @Test
    void Student_can_send_friendship_request_to_others_if_they_are_not_found_in_any_of_her_lists() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Student friend1 = new Student("810197001", "pete");
        Student friend2 = new Student("810197002", "mike");
        Student friend3 = new Student("810197003", "noel");
        Student friend4 = new Student("810197004", "jack");

        bebe.getFriends().add(friend1);
        bebe.getFriends().add(friend2);
        bebe.getRequested().add(friend3);

        bebe.sendFriendshipRequest(friend4);

        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_cannot_send_request_to_herself() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197000", "lily");

        try {
            bebe.sendFriendshipRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "You cannot send friendship request to yourself.");
    }

    @Test
    void Students_cannot_receive_friendship_request_if_they_have_blocked_the_other_student() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        friend.getBlocked().add(bebe);

        try {
            friend.receiveFriendshipRequest(bebe);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "You have been blocked by this user.");
    }

    @Test
    void Friendships_are_removed_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Student friend1 = new Student("810197001", "pete");
        Student friend2 = new Student("810197002", "mike");
        Student friend3 = new Student("810197003", "kent");
        Student friend4 = new Student("810197004", "nile");
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
    void Friendships_cannot_be_removed_without_any_relation() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");

        try {
            friend.removeFriend(bebe);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "There is no relation between these students.");
    }

    @Test
    void All_friends_are_returned_correctly() {
        Student bebe = new Student("810197000", "bebe");
        Student friend1 = new Student("810197001", "pete");
        Student friend2 = new Student("810197002", "mike");
        Student friend3 = new Student("810197003", "kent");
        Student friend4 = new Student("810197004", "nile");
        bebe.getFriends().add(friend1);
        bebe.getRequested().add(friend2);
        bebe.getBlocked().add(friend3);
        bebe.getPending().add(friend4);
        assertThat(bebe.getAllFriends()).hasSize(4);
    }

    @Test
    void Student_are_accepted_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getRequested().add(friend);
        bebe.acceptRequest(friend);
        assertThat(bebe.getFriends()).hasSize(1);
    }

    @Test
    void Student_cannot_accept_friends_who_are_not_in_her_requested_list() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        Student friend = new Student("810197001", "pete");
        bebe.getBlocked().add(friend);

        try {
            bebe.acceptRequest(friend);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "This user did not request to be your friend.");
    }

}
