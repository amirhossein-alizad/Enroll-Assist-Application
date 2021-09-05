package ir.proprog.enrollassist.domain.enrollmentList;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.CourseRequestedTwice;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.MaxCreditsLimitExceeded;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    private Course math1, prog, ap, math2, eco , ds, phys1, signal, maaref, english, phys2;
    private Section math1_1, math1_2, prog_1, ap_1, math2_1, eco_1, ds_1, signal_1, phys1_1, maaref1_1, english_1, phys2_1;
    Student bebe;
    @BeforeEach
    void setUp() throws ExceptionList {
        bebe = mock(Student.class);

        math1 = new Course("1111111", "MATH1", 3, "Undergraduate");
        prog = new Course("2222222", "PROG", 4, "Undergraduate");
        ap = new Course("3333333", "AP", 3, "Undergraduate").withPre(prog);
        math2 = new Course("4444444", "MATH2", 3, "Undergraduate").withPre(math1);
        eco = new Course("5555555", "ECO", 3, "Undergraduate");
        ds = new Course("6666666", "DS", 3, "Undergraduate");
        phys1 = new Course("7777777", "PHYS1", 4, "Undergraduate");
        signal = new Course("8888888", "SIGNAL", 4, "Undergraduate");
        maaref = new Course("9999999", "MAAREF", 4, "Undergraduate");
        english = new Course("1010101", "EN", 4, "Undergraduate");
        phys2 = new Course("1313131", "PHYS2", 3, "Undergraduate").withPre(phys1, math1);

        prog_1 = new Section(prog, "01");
        maaref1_1 = new Section(maaref, "01");
        english_1 = new Section(english, "01");
        math1_1 = new Section(math1, "01");
        math1_2 = new Section(math1, "02");
        phys1_1 = new Section(phys1, "01");
        phys2_1 = new Section(phys2, "01");
        signal_1 = new Section(signal, "01");
        eco_1 = new Section(eco, "01");
        ds_1 = new Section(ds, "01");
        math2_1 = new Section(math2, "01");
        ap_1 = new Section(ap, "01");
    }

    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() {
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() {
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1);
        assertThat(list.checkHasNotAlreadyPassedCourses())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() {
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, math1_2);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(11.5));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(16));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, phys1_1, signal_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, phys1_1, signal_1, ap_1, english_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() throws Exception {
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, phys1_1, signal_1, eco_1, ds_1, english_1, ds_1);
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
    void New_students_cant_take_more_than_twenty_credits() {
        when(bebe.calculateGPA()).thenReturn(new Grade());
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, ds_1, math1_2, signal_1, eco_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_with_GPA_more_than_12_can_take_more_than_14_credits() throws Exception {
        when(bebe.calculateGPA()).thenReturn(new Grade(12.0));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, math1_1, ap_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_with_GPA_more_than_17_can_take_more_than_20_credits() throws Exception {
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(new Grade(17.01));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog_1, maaref1_1, english_1, math2_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Requesting_courses_with_two_prerequisites_where_none_has_been_passed_violates_two_rules() throws Exception {
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog_1, maaref1_1, eco_1, maaref1_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(new Grade(15));
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_courses_with_2_prerequisites_when_one_has_been_passed_is_a_violation() throws Exception {
        Course phys2 = new Course("1111111", "PHYS2", 3, "Undergraduate").withPre(math1, phys1);
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys2_1);
        when(bebe.calculateGPA()).thenReturn(new Grade(15));
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites())
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_conflicting_time() throws Exception {
        math1_1.setExamTime(new ExamTime("2021-06-21T08:00", "2021-06-21T11:00"));
        phys2_1.setExamTime(new ExamTime("2021-06-21T09:30", "2021-06-21T13:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_may_have_sections_intersecting_at_one_time() throws Exception {
        math1_1.setExamTime(new ExamTime("2021-06-21T11:00", "2021-06-21T13:00"));
        phys2_1.setExamTime(new ExamTime("2021-06-21T13:00", "2021-06-21T16:00"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_one_section_when_its_exam_time_is_colliding_with_two_other_sections() throws Exception {
        math1_1.setExamTime(new ExamTime("2021-06-21T08:00", "2021-06-21T11:30"));
        phys2_1.setExamTime(new ExamTime("2021-06-21T11:00", "2021-06-21T14:00"));
        prog_1.setExamTime(new ExamTime("2021-06-21T13:30", "2021-06-21T16:30"));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, prog_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Students_cant_take_less_than_twelve_credits() throws Exception {
        EnrollmentList list1 = new EnrollmentList("list", bebe);
        list1.addSections(new Section(math1, "01"));
        when(bebe.calculateGPA()).thenReturn(new Grade(16));
        when(bebe.getGraduateLevel()).thenReturn(GraduateLevel.Undergraduate);
        when(bebe.getTotalTakenCredits()).thenReturn(18);
        assertThat(list1.checkValidGPALimit())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_conflict_in_section_schedules() throws Exception {
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Monday", "11:00", "13:00");
        PresentationSchedule p4 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        math1_1.setPresentationSchedule(Set.of(p1, p2));
        phys2_1.setPresentationSchedule(Set.of(p3, p4));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkSectionScheduleConflicts())
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void Number_of_new_conflicts_are_zero_when_examTime_set_to_time_that_has_no_conflicts_with_others() throws Exception {
        Course da = new Course("6666666", "DA", 4, "Undergraduate");
        Course ds = new Course("7777777", "DS", 4, "Undergraduate");
        Course dm = new Course("8888888", "DM", 4, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        Section da_1 = new Section(da, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ds_1 = new Section(ds, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section dm_1 = new Section(dm, "01", new ExamTime("2021-08-21T11:00", "2021-08-21T13:00"), Set.of(p3));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(da_1, ds_1, dm_1);
        assertThat(list.makeExamTimeConflict(da_1, new ExamTime("2021-08-21T08:00", "2021-08-21T10:00")))
                .isEqualTo(false);
    }

    @Test
    void Number_of_new_conflicts_are_not_zero_when_examTime_set_to_time_that_has_conflicts_with_others() throws Exception {
        Course da = new Course("6666666", "DA", 4, "Undergraduate");
        Course ds = new Course("7777777", "DS", 4, "Undergraduate");
        Course dm = new Course("8888888", "DM", 4, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        Section da_1 = new Section(da, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ds_1 = new Section(ds, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section dm_1 = new Section(dm, "01", new ExamTime("2021-08-21T11:00", "2021-08-21T13:00"), Set.of(p3));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(da_1, ds_1, dm_1);
        assertThat(list.makeExamTimeConflict(da_1, new ExamTime("2021-08-21T13:30", "2021-08-21T16:30")))
                .isEqualTo(true);
    }

    @Test
    void Number_of_new_conflicts_are_not_zero_when_Schedule_set_to_time_that_has_conflicts_with_others_1() throws Exception {
        Course da = new Course("6666666", "DA", 4, "Undergraduate");
        Course ds = new Course("7777777", "DS", 4, "Undergraduate");
        Course dm = new Course("8888888", "DM", 4, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        Section da_1 = new Section(da, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ds_1 = new Section(ds, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section dm_1 = new Section(dm, "01", new ExamTime("2021-08-21T11:00", "2021-08-21T13:00"), Set.of(p3));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(da_1, ds_1, dm_1);
        assertThat(list.makePresentationScheduleConflict(ds_1, List.of(new PresentationSchedule("Monday", "10:00", "11:00"))))
                .isEqualTo(true);
    }


    @Test
    void Number_of_new_conflicts_are_not_zero_when_Schedule_set_to_time_that_has_conflicts_with_others_2() throws Exception {
        Course da = new Course("6666666", "DA", 4, "Undergraduate");
        Course ds = new Course("7777777", "DS", 4, "Undergraduate");
        Course dm = new Course("8888888", "DM", 4, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Monday", "10:00", "11:00");
        Section da_1 = new Section(da, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ds_1 = new Section(ds, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section dm_1 = new Section(dm, "01", new ExamTime("2021-08-21T11:00", "2021-08-21T13:00"), Set.of(p3));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(da_1, ds_1, dm_1);
        assertThat(list.makePresentationScheduleConflict(da_1, List.of(new PresentationSchedule("Wednesday", "09:00", "11:00"))))
                .isEqualTo(true);
    }

    @Test
    void Number_of_new_conflicts_are_zero_when_Schedule_set_to_time_that_has_no_conflicts_with_others() throws Exception {
        Course da = new Course("6666666", "DA", 4, "Undergraduate");
        Course ds = new Course("7777777", "DS", 4, "Undergraduate");
        Course dm = new Course("8888888", "DM", 4, "Undergraduate");
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Monday", "10:00", "11:00");
        Section da_1 = new Section(da, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ds_1 = new Section(ds, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section dm_1 = new Section(dm, "01", new ExamTime("2021-08-21T11:00", "2021-08-21T13:00"), Set.of(p3));
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(da_1, ds_1, dm_1);
        assertThat(list.makePresentationScheduleConflict(da_1, List.of(new PresentationSchedule("Wednesday", "09:00", "10:30"))))
                .isEqualTo(false);
    }
}

