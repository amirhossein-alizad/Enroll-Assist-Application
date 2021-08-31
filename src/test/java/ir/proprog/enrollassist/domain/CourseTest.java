package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.student.Student;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class CourseTest {

    @Test
    void Course_with_empty_field_cant_be_created() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            Course math1 = new Course("", "", 3);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(), "{\"1\":\"Course must have a name.\"," +
                                                        "\"2\":\"Course number cannot be empty.\"}");
    }

    @Test
    void Course_with_empty_invalid_course_number_cant_be_created() {
        ExceptionList exceptionList = new ExceptionList();
        try {
            Course math1 = new Course("1", "C", -3);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertEquals(exceptionList.toString(),
                "{\"1\":\"Course number must contain 7 numbers.\","
                + "\"2\":\"Credit must be one of the following values: 0, 1, 2, 3, 4.\"}");
    }

    @Test
    void Course_with_no_prerequisites_can_be_taken_by_anybody() {
        ExceptionList exceptionList = new ExceptionList();
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        Course math1 = null;
        try {
            math1 = new Course("1111111", "MATH1", 3);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
        assertThat(math1.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_one_prerequisite_can_be_taken_by_who_has_passed_pre() {
        ExceptionList exceptionList = new ExceptionList();
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        Course math1 = null;
        try {
            math1 = new Course("1111111", "MATH1", 3);
            Course math2 = new Course("2222222", "MATH2", 3).withPre(math1);
            assertThat(math2.canBeTakenBy(bebe))
                    .isNotNull()
                    .isEmpty();
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    void Course_with_two_prerequisites_can_be_taken_by_who_has_passed_both_pres() {
        ExceptionList exceptionList = new ExceptionList();
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        try {
            Course math1 = new Course("1111111", "MATH1", 3);
            Course phys1 = new Course("2222222", "PHYS1", 3);
            Course phys2 = new Course("3333333", "PHYS2", 3).withPre(math1, phys1);
            assertThat(phys2.canBeTakenBy(bebe))
                    .isNotNull()
                    .isEmpty();
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_one_has_not_been_passed() {
        ExceptionList exceptionList = new ExceptionList();
        Student bebe = mock(Student.class);
        try {
            Course math1 = new Course("1111111", "MATH1", 3);
            Course phys1 = new Course("2222222", "PHYS1", 3);
            Course phys2 = new Course("3333333", "PHYS2", 3).withPre(math1, phys1);
            when(bebe.hasPassed(math1)).thenReturn(true);
            when(bebe.hasPassed(phys1)).thenReturn(false);
            assertThat(phys2.canBeTakenBy(bebe))
                    .isNotNull()
                    .hasSize(1);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_none_has_been_passed() {
        ExceptionList exceptionList = new ExceptionList();
        Student bebe = mock(Student.class);
        try {
            Course math1 = new Course("1111111", "MATH1", 3);
            Course phys1 = new Course("2222222", "PHYS1", 3);
            Course phys2 = new Course("3333333", "PHYS2", 3).withPre(math1, phys1);
            when(bebe.hasPassed(math1)).thenReturn(false);
            when(bebe.hasPassed(phys1)).thenReturn(false);
            assertThat(phys2.canBeTakenBy(bebe))
                    .isNotNull()
                    .hasSize(2);
        }catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        assertFalse(exceptionList.hasException());
    }
}
