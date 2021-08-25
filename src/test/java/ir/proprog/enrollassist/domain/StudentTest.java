package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class StudentTest {

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed() throws Exception {
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4444444", "MATH1", 3);
        bebe.setGrade("13981", math1, 9.99);
        assertThat(bebe.hasPassed(math1))
                .isFalse();
    }

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed_But_Calculated_in_GPA() throws Exception {
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys1 = new Course("8888888", "PHYS1", 3);
        Course prog = new Course("7777777", "PROG", 4);
        Course economy = new Course("1111111", "ECO", 3);
        Course maaref = new Course("5555555", "MAAREF", 2);
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
        Course math1 = new Course("1111111", "MATH1", 3);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Student_has_passed_records_that_are_in_grades_set()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        bebe.setGrade("13981", math1, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        bebe.setGrade("13981", math1, 19);
        bebe.setGrade("13981", prog, 17);
        bebe.setGrade("13981", andishe, 19);
        assertThat(bebe.calculateGPA().getGrade())
                .isEqualTo(18.25);
    }

    @Test
    void Student_returns_takeable_courses_with_no_prerequisites_correctly() throws Exception{
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(math1, prog, andishe);
    }


    @Test
    void Student_does_not_return_passed_courses_as_takeable() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        bebe.setGrade("13981", math1, 20);
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(prog, andishe);
    }

    @Test
    void Student_returns_courses_with_passed_prerequisites_as_takeable_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Course math2 = new Course("4444444", "MATH2", 3).withPre(math1);
        bebe.setGrade("13981", math1, 20);
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(prog, andishe, math2);
    }

    @Test
    void Student_does_not_return_courses_which_prerequisites_are_not_passed_as_takeable_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Course math2 = new Course("4444444", "MATH2", 3).withPre(math1);
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableCourses())
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(prog, andishe, math1);
    }

    @Test
    void Student_returns_takeable_sections_which_courses_have_no_prerequisites_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Section math1_1 = new Section(math1, "01");
        Section math1_2 = new Section(math1, "02");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableSections(List.of(math1_1, math1_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math1_1, math1_2, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_their_courses_are_passed_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        bebe.setGrade("13981", math1, 20);
        Major major = new Major("123", "CE");
        major.addCourse(math1, prog, andishe);
        bebe.setMajor(major);
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
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Course math2 = new Course("4444444", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        bebe.setGrade("13981", math1, 20);
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrder(math2_2, math2_1, prog1_1, andishe1_1);
    }

    @Test
    void Student_does_not_return_sections_which_prerequisites_for_their_courses_have_not_been_passed_correctly() throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        Course math2 = new Course("4444444", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section andishe1_1 = new Section(andishe, "01");
        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        Major major = new Major("123", "CE");
        major.addCourse(math1, math2, prog, andishe);
        bebe.setMajor(major);
        assertThat(bebe.getTakeableSections(List.of(math1_1, math2_1, math2_2, prog1_1, andishe1_1)))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(math1_1, prog1_1, andishe1_1);
    }

    @Test
    void Grade_of_course_with_valid_term_can_set_correctly() {
        String error = "";
        Student bebe = new Student("810197000", "bebe");
        try {
            Course math1 = new Course("1111111", "MATH1", 3);
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
            Course math1 = new Course("1111111", "MATH1", 3);
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
}
