package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseTest {

    private ExceptionList errors;
    private Course math1, phys1, phys2;
    private Student bebe;

    @BeforeEach
    void setUp() throws Exception{
        errors = new ExceptionList();
        math1 = new TestCourseBuilder()
                .courseNumber("1111111")
                .title("MATH1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        phys1 = new TestCourseBuilder()
                .courseNumber("2222222")
                .title("PHYS1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        phys2 = new TestCourseBuilder()
                .courseNumber("3333333")
                .title("PHYS2")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build()
                .withPre(math1, phys1);
        bebe = mock(Student.class);

    }

    @Test
    void Course_with_empty_field_cant_be_created() {
        try {
            Course math1 = new TestCourseBuilder()
                    .courseNumber("")
                    .title("")
                    .credits(3)
                    .graduateLevel("Undergraduate")
                    .build();
        } catch (ExceptionList e) {
            errors.addExceptions(e.getExceptions());
        }
        assertEquals(errors.toString(), "{\"1\":\"Course must have a name.\"," +
                "\"2\":\"Course number cannot be empty.\"}");
    }

    @Test
    void Course_with_empty_invalid_course_number_cant_be_created() {
        try {
            Course math1 = new TestCourseBuilder()
                    .courseNumber("1")
                    .title("MATH1")
                    .credits(-3)
                    .graduateLevel("Undergraduate")
                    .build();
        } catch (ExceptionList e) {
            errors.addExceptions(e.getExceptions());
        }
        assertEquals(errors.toString(),
                "{\"1\":\"Course number must contain 7 numbers.\","
                        + "\"2\":\"Credit must be one of the following values: 0, 1, 2, 3, 4.\"}");
    }

    @Test
    void Course_with_no_prerequisites_can_be_taken_by_anybody() {
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        Course math1 = null;
        try {
            math1 = new TestCourseBuilder()
                    .courseNumber("1111111")
                    .title("MATH1")
                    .credits(3)
                    .graduateLevel("Undergraduate")
                    .build();
        } catch (ExceptionList e) {
            errors.addExceptions(e.getExceptions());
        }
        assertFalse(errors.hasException());
        assertThat(math1.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_one_prerequisite_can_be_taken_by_who_has_passed_pre() {
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        Course math1;
        try {
            math1 = new TestCourseBuilder()
                    .courseNumber("1111111")
                    .title("MATH1")
                    .credits(3)
                    .graduateLevel("Undergraduate")
                    .build();
            Course math2 = new TestCourseBuilder()
                    .courseNumber("2222222")
                    .title("MATH2")
                    .credits(3)
                    .graduateLevel("Undergraduate")
                    .build()
                    .withPre(math1);
            assertThat(math2.canBeTakenBy(bebe))
                    .isNotNull()
                    .isEmpty();
        } catch (ExceptionList e) {
            errors.addExceptions(e.getExceptions());
        }
        assertFalse(errors.hasException());
    }

    @Test
    void Course_with_two_prerequisites_can_be_taken_by_who_has_passed_both_pres() {
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_one_has_not_been_passed() {
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_none_has_been_passed() {
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void EducationGrade_compared_correctly() throws Exception {
        Course math1 = new TestCourseBuilder()
                .courseNumber("1111111")
                .title("MATH1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        assertTrue(math1.equalsEducationGrade(GraduateLevel.valueOf("Undergraduate")));
        assertFalse(math1.equalsEducationGrade(GraduateLevel.valueOf("PHD")));
    }
}
