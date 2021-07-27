package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
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

    @Test
    void Enrollment_list_max_limit_rule_fails_gpa_less_than_17() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(16F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_max_limit_rule_fails_with_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Course flat = new Course("9", "FLAT", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        Section flt_1 = new Section(flat, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1, flt_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_max_limit_rule_passes_1() {
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
        when(bebe.calculateGPA()).thenReturn(15F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_multiple_rules_fail_1() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(CourseRequestedTwice.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditLimitExceeded.class);
    }

    @Test
    void Enrollment_list_multiple_rules_fail_2() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Course ds = new Course("6", "DS", 3);
        Course da = new Course("7", "DS", 3);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.calculateGPA()).thenReturn(11F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1, ds_1, da_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditLimitExceeded.class);
    }

    @Test
    void Enrollment_list_multiple_rules_fail_3() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course eco = new Course("3", "ECO", 3);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.calculateGPA()).thenReturn(11F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, eco_1, eco_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(3);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(CourseRequestedTwice.class);
        assertThat(violations.get(2).getClass())
                .isEqualTo(MinCreditLimitNotMet.class);
    }

    @Test
    void Enrollment_list_multiple_rules_fail_4() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course eco = new Course("3", "ECO", 3);
        Course prog = new Course("2", "PROG", 4);
        Course da = new Course("7", "DS", 3).withPre(prog);
        Course dm = new Course("8", "DM", 3);
        Section math1_1 = new Section(math1, "01");
        Section eco_1 = new Section(eco, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(12F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, eco_1, eco_1, da_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(3);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(RequestedCourseAlreadyPassed.class);
        assertThat(violations.get(2).getClass())
                .isEqualTo(CourseRequestedTwice.class);
    }
}

