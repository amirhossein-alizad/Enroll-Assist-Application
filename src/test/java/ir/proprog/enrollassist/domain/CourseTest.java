package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CourseTest {

    @Test
    void Course_with_no_prerequisites_can_be_taken_by_anybody() {
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        Course math1 = new Course("1", "MATH1", 3);
        assertThat(math1.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_one_prerequisite_can_be_taken_by_who_has_passed_pre() {
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        Course math1 = new Course("1", "MATH1", 3);
        Course math2 = new Course("2", "MATH2", 3).withPre(math1);
        assertThat(math2.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_two_prerequisites_can_be_taken_by_who_has_passed_both_pres() {
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(true);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course phys2 = new Course("3", "PHYS2", 3).withPre(math1, phys1);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_one_has_not_been_passed() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course phys2 = new Course("3", "PHYS2", 3).withPre(math1, phys1);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Course_with_two_prerequisites_cannot_be_taken_if_none_has_been_passed() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course phys2 = new Course("3", "PHYS2", 3).withPre(math1, phys1);
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.canBeTakenBy(bebe))
                .isNotNull()
                .hasSize(2);
    }
}
