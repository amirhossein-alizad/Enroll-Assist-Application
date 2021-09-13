package ir.proprog.enrollassist.domain.student;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.program.ProgramType;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudentTest {
    private Student bebe;
    private TestCourseBuilder testCourseBuilder = new TestCourseBuilder();
    private Course math1, phys1, prog, economy, maaref, andishe, math2;
    private Section math1_1, prog1_1, andishe1_1, math2_2, math2_1, math1_2;
    private Major major;
    private Program program;

    @BeforeEach
    void setUp() throws Exception{
        math1 = testCourseBuilder
                .build();
        phys1 = testCourseBuilder
                .courseNumber("1111112")
                .build();
        prog = testCourseBuilder
                .courseNumber("1111113")
                .credits(4)
                .build();
        economy = testCourseBuilder
                .courseNumber("1111114")
                .credits(3)
                .build();
        maaref = testCourseBuilder
                .courseNumber("1111115")
                .credits(2)
                .build();
        andishe = testCourseBuilder
                .courseNumber("1111116")
                .build();
        math2 = testCourseBuilder
                .courseNumber("1111117")
                .credits(3)
                .build().withPre(math1);
        Major major = new Major("123", "CE");
        program = new Program(major, "Undergraduate", 140, 140, "Major");
        program.addCourse(math1, phys1, prog, economy, maaref, andishe, math2);
        bebe = new TestStudentBuilder()
                .withGraduateLevel("Undergraduate")
                .build()
                .addProgram(program);
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
    void Student_can_take_course_in_Minor_program_without_checking_prerequisites() throws ExceptionList {
        Student student = new TestStudentBuilder().withStudentNumber("810197001").withGraduateLevel("Undergraduate").build();
        Program p = new Program(major, "Undergraduate", 12, 12, "Minor").addCourse(math2);
        student.addProgram(p);
        assertEquals(student.canTake(math2), List.of());
    }


}
