package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

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

    @Test
    void Enrollment_list_course_already_passed_rule_fails() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, prog_1);
//        System.out.println(list.checkEnrollmentRules());
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_course_requested_twice_rule_fails() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog_1 = new Section(prog, "01");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, prog_1, math1_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_min_limit_rule_fails() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("3", "PROG", 4);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_max_limit_rule_fails_gpa_less_than_12() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        bebe.setGrade("1", math1, 11);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course ds = new Course("6", "DS", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(11.5F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }
}
