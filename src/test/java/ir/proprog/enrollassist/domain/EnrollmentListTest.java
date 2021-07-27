package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_prerequisite_rule_fails() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, prog_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }
}
