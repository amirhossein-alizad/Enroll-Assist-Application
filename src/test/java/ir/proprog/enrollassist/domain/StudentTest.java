package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class StudentTest {

    @Test
    void a_Study_Record_With_Grade_Less_Than_Ten_is_Not_Passed() throws Exception {
        Student bebe = new Student("810197546", "bebe");
        Course math1 = new Course("4444444", "MATH1", 3);
        bebe.setGrade("01", math1, 9.99);
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
        bebe.setGrade("t1", math1, 15.5);
        bebe.setGrade("t1", phys1, 9);
        bebe.setGrade("t1", prog, 17.25);
        bebe.setGrade("t1", economy, 19.5);
        bebe.setGrade("t1", maaref, 16);
        assertThat(bebe.calculateGPA())
                .isEqualTo(15.53F);
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
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly()  throws Exception {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        bebe.setGrade("3900", math1, 19);
        bebe.setGrade("3900", prog, 17);
        bebe.setGrade("3900", andishe, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(18.25F);
    }

    @Test
    void Student_returns_takeable_courses_with_no_prerequisites_correctly() throws Exception{
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 3);
        Course andishe = new Course("3333333", "ANDISHE", 2);
        assertThat(bebe.getTakeableCourses(List.of(math1, prog, andishe)))
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
        bebe.setGrade("t1", math1, 20);
        assertThat(bebe.getTakeableCourses(List.of(math1, prog, andishe)))
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
        bebe.setGrade("t1", math1, 20);
        assertThat(bebe.getTakeableCourses(List.of(math1, prog, andishe, math2)))
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
        assertThat(bebe.getTakeableCourses(List.of(math1, prog, andishe, math2)))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(prog, andishe, math1);
    }

}
