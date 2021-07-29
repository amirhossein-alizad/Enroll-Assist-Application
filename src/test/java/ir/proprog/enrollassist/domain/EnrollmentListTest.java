package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_cannot_have_courses_with_prerequisites_not_taken_by_owner() {
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
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course ap = new Course("3", "AP", 3).withPre(prog);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section math2_1 = new Section(math2, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() {
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
        assertThat(list.checkHasNotAlreadyPassedCourses(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course phys1 = new Course("2", "PHYS1", 3);
        Course prog = new Course("3", "PROG", 4);
        Section math1_1 = new Section(math1, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section prog_1 = new Section(prog, "01");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, phys1_1, prog_1, math1_1);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_belonging_to_student_with_gpa_less_than_12_cannot_take_more_than_14_credits() {
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
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_belonging_to_student_with_gpa_less_than_17_cannot_take_more_than_20_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 6);
        Course ap = new Course("4", "AP", 9);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(16F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1, ap_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 8);
        Course eco = new Course("3", "ECO", 12);
        Course math2 = new Course("5", "MATH2", 6).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, eco_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_() {
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
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() {
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
    void Students_cannot_take_courses_without_their_prerequisites_taken_and_more_than_14_credits_with_gpa_less_than_12() {
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
    void Enrollment_list_cannot_have_duplicate_courses_and_their_prerequisites_not_taken() {
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
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(PrerequisiteNotTaken.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(CourseRequestedTwice.class);
    }
}

