package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudentTest {
    @Test
    void Student_has_passed_records_in_grades_set() {
        Student bebe = mock(Student.class);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        Course math1 = new Course("1", "MATH1", 3);
        assertThat(math1.canBeTakenBy(bebe))
                .isNotNull()
                .isEmpty();
    }
}
