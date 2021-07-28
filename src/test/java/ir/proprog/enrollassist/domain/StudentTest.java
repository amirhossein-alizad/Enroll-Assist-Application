package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudentTest {
    @Test
    void Student_has_not_passed_records_that_are_not_in_grades_set() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(false);
    }

    @Test
    void Student_has_passed_records_that_are_in_grades_set() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.hasPassed(math1))
                .isEqualTo(true);
    }

    @Test
    void Student_gpa_with_one_study_record_is_returned_correctly() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("3900", math1, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(19);
    }

    @Test
    void Student_gpa_with_multiple_study_records_is_returned_correctly() {
        Student bebe = new Student("810197000", "bebe");
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 3);
        Course andishe = new Course("3", "ANDISHE", 2);
        bebe.setGrade("3900", math1, 19);
        bebe.setGrade("3900", prog, 17);
        bebe.setGrade("3900", andishe, 19);
        assertThat(bebe.calculateGPA())
                .isEqualTo(18.25F);
    }
}
