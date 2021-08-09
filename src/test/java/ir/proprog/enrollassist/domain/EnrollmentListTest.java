package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.EnrollmentRules.CourseRequestedTwice;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.MaxCreditsLimitExceeded;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("3333333", "PROG", 4);
        Course ap = new Course("3333333", "AP", 3).withPre(prog);
        Course math2 = new Course("5555555", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Section math1_1 = new Section(math1, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1);
        assertThat(list.checkHasNotAlreadyPassedCourses(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Section math1_1 = new Section(math1, "01");
        Section math1_2 = new Section(math1, "02");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, math1_2);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 4);
        Course eco = new Course("3333333", "ECO", 3);
        Course ap = new Course("4444444", "AP", 3);
        Course ds = new Course("6666666", "DS", 3);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.calculateGPA()).thenReturn(11.5F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 4);
        Course eco = new Course("3333333", "ECO", 6);
        Course ap = new Course("4444444", "AP", 9);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.calculateGPA()).thenReturn(16F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 6);
        Course prog = new Course("2222222", "PROG", 8);
        Course eco = new Course("3333333", "ECO", 12);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.calculateGPA()).thenReturn(20F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

//    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits()  throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 6);
        Course math2 = new Course("5555555", "MATH2", 8).withPre(math1);
        Course da = new Course("7777777", "DS", 8);
        Course dm = new Course("8888888", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, da_1, dm_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(CourseRequestedTwice.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditsLimitExceeded.class);
    }

    @Test
    void New_students_cant_take_more_than_twenty_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course phys1 = new Course("8888888", "PHYS1", 6);
        Course prog = new Course("7777777", "PROG", 8);
        Course maaref = new Course("5555555", "MAAREF", 4);
        Course dm = new Course("3333333", "DM", 6);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        when(bebe.calculateGPA()).thenReturn(0.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1);
        assertThat(list1.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_with_GPA_more_than_12_can_take_more_than_14_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 6);
        Course phys1 = new Course("8888888", "PHYS1", 6);
        Course prog = new Course("7777777", "PROG", 7);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section math1_1 = new Section(math1, "01");
        when(bebe.calculateGPA()).thenReturn(12.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, math1_1);
        assertThat(list1.checkValidGPALimit(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_with_GPA_more_than_17_can_take_more_than_20_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 6);
        Course phys1 = new Course("8888888", "PHYS1", 6);
        Course prog = new Course("7777777", "PROG", 8);
        Course maaref = new Course("5555555", "MAAREF", 4);

        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(17.01F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, maaref1_1);
        assertThat(list1.checkValidGPALimit(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Requesting_courses_with_two_prerequisites_where_none_has_been_passed_violates_two_rules() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys1 = new Course("8888888", "PHYS1", 3);
        Course prog = new Course("7777777", "PROG", 4);
        Course maaref = new Course("5555555", "MAAREF", 2);
        Course economy = new Course("1111111", "ECO", 3);
        Course akhlagh = new Course("1122112", "AKHLAGH", 2);
        Course phys2 = new Course("9999999", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_courses_with_2_prerequisites_when_one_has_been_passed_is_a_violation() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys1 = new Course("8888888", "PHYS1", 3);
        Course phys2 = new Course("9999999", "PHYS2", 3).withPre(math1, phys1);
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys2 = new Course("9999999", "PHYS2", 3);
        Section math1_1 = new Section(math1, "01");
        math1_1.setExamTime(new ExamTime("2021-06-21T14:00:00", "2021-06-21T17:00:00"));
        Section phys2_1 = new Section(phys2, "01");
        phys2_1.setExamTime(new ExamTime("2021-06-21T14:00:00", "2021-06-21T17:00:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_conflicting_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys2 = new Course("9999999", "PHYS2", 3);
        Section math1_1 = new Section(math1, "01");
        math1_1.setExamTime(new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00"));
        Section phys2_1 = new Section(phys2, "01");
        phys2_1.setExamTime(new ExamTime("2021-06-21T9:30:00", "2021-06-21T13:00:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_may_have_sections_on_the_different_days_and_conflicting_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys2 = new Course("9999999", "PHYS2", 3);
        Section math1_1 = new Section(math1, "01");
        math1_1.setExamTime(new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00"));
        Section phys2_1 = new Section(phys2, "01");
        phys2_1.setExamTime(new ExamTime("2021-06-23T9:30:00", "2021-06-23T13:00:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_may_have_sections_intersecting_at_one_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys2 = new Course("9999999", "PHYS2", 3);
        Section math1_1 = new Section(math1, "01");
        math1_1.setExamTime(new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:00:00"));
        Section phys2_1 = new Section(phys2, "01");
        phys2_1.setExamTime(new ExamTime("2021-06-21T11:00:00", "2021-06-21T13:00:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_one_section_when_its_exam_time_is_colliding_with_two_other_sections() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course ap = new Course("2222222", "AP", 4);
        Course phys2 = new Course("3333333", "PHYS2", 3);
        Section math1_1 = new Section(math1, "01");
        Section phys2_1 = new Section(phys2, "01");
        Section ap1_1 = new Section(ap, "01");
        math1_1.setExamTime(new ExamTime("2021-06-21T8:00:00", "2021-06-21T11:30:00"));
        ap1_1.setExamTime(new ExamTime("2021-06-21T11:00:00", "2021-06-21T14:00:00"));
        phys2_1.setExamTime(new ExamTime("2021-06-21T13:30:00", "2021-06-21T16:30:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, ap1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(2);
    }

}
