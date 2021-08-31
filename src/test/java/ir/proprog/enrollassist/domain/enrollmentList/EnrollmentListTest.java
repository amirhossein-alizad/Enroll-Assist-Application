package ir.proprog.enrollassist.domain.enrollmentList;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.CourseRequestedTwice;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.MaxCreditsLimitExceeded;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() throws ExceptionList {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 4);
        Course ap = new Course("3333333", "AP", 3).withPre(prog);
        Course math2 = new Course("5555555", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() throws ExceptionList {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Section math1_1 = new Section(math1, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1);
        assertThat(list.checkHasNotAlreadyPassedCourses())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() throws ExceptionList {
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
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course prog = new Course("2222222", "PROG", 4);
        Course eco = new Course("3333333", "ECO", 3);
        Course ap = new Course("4444444", "AP", 3);
        Course ds = new Course("5555555", "DS", 3);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade(11.5));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 4);
        Course prog = new Course("2222222", "PROG", 4);
        Course eco = new Course("3333333", "ECO", 4);
        Course ap = new Course("4444444", "AP", 4);
        Course phys1 = new Course("5555555", "PHYS1", 4);
        Course signal = new Course("6666666", "SIGNAL", 4);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section signal_1 = new Section(signal, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade(16));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, phys1_1, signal_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 4);
        Course prog = new Course("2222222", "PROG", 4);
        Course eco = new Course("3333333", "ECO", 4);
        Course phys1 = new Course("4444444", "PHYS1", 4);
        Course signal = new Course("5555555", "SIGNAL", 4);
        Course ap = new Course("6666666", "AP", 4);
        Course ie = new Course("7777777", "IE", 4);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section phys1_1 = new Section(phys1, "01");
        Section signal_1 = new Section(signal, "01");
        Section ap_1 = new Section(ap, "01");
        Section ie_1 = new Section(ie, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, phys1_1, signal_1, ap_1, ie_1);
        assertThat(list.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 4);
        Course prog = new Course("2222222", "PROG", 4);
        Course ethics = new Course("3333333", "ETHICS", 4);
        Course math2 = new Course("5555555", "MATH2", 4).withPre(math1);
        Course da = new Course("6666666", "DA", 4);
        Course ds = new Course("7777777", "DS", 4);
        Course dm = new Course("8888888", "DM", 4);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section ethics_1 = new Section(ethics, "01");
        Section da_1 = new Section(da, "01");
        Section ds_1 = new Section(ds, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(new Grade(20));
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, da_1, ds_1, ethics_1, dm_1, dm_1);
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
    void New_students_cant_take_more_than_twenty_credits() throws ExceptionList {
        Student bebe = mock(Student.class);
        Course phys1 = new Course("1111111", "PHYS1", 4);
        Course prog = new Course("2222222", "PROG", 4);
        Course maaref = new Course("3333333", "MAAREF", 4);
        Course dm = new Course("4444444", "DM", 4);
        Course akhlagh = new Course("5555555", "AKHLAGH", 4);
        Course db = new Course("6666666", "DB", 4);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section db_1 = new Section(db, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1, akhlagh_1, db_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_with_GPA_more_than_12_can_take_more_than_14_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 4);
        Course phys1 = new Course("2222222", "PHYS1", 4);
        Course prog = new Course("3333333", "PROG", 4);
        Course ap = new Course("4444444", "AP", 4);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section math1_1 = new Section(math1, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.calculateGPA()).thenReturn(new Grade(12.0));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, math1_1, ap_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_with_GPA_more_than_17_can_take_more_than_20_credits() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 4);
        Course phys1 = new Course("2222222", "PHYS1", 4);
        Course prog = new Course("3333333", "PROG", 4);
        Course maaref = new Course("4444444", "MAAREF", 4);
        Course english = new Course("5555555", "EN", 4);
        Course farsi = new Course("6666666", "FA", 3);

        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section english_1 = new Section(english, "01");
        Section farsi_1 = new Section(farsi, "01");

        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(new Grade(17.01));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, maaref1_1, english_1, farsi_1);
        assertThat(list1.checkValidGPALimit())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Requesting_courses_with_two_prerequisites_where_none_has_been_passed_violates_two_rules() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3);
        Course phys1 = new Course("2222222", "PHYS1", 3);
        Course prog = new Course("3333333", "PROG", 4);
        Course maaref = new Course("4444444", "MAAREF", 2);
        Course economy = new Course("5555555", "ECO", 3);
        Course akhlagh = new Course("6666666", "AKHLAGH", 2);
        Course phys2 = new Course("7777777", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(new Grade(15));
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkHasPassedAllPrerequisites())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_courses_with_2_prerequisites_when_one_has_been_passed_is_a_violation() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1212121", "MATH1", 3);
        Course phys1 = new Course("1313131", "PHYS1", 3);
        Course phys2 = new Course("1111111", "PHYS2", 3).withPre(math1, phys1);
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
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1234567", "MATH1", 3).setHasExam(true);
        Course phys2 = new Course("2345678", "PHYS2", 3).setHasExam(true);
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T14:00", "2021-06-21T17:00"),  Collections.emptySet());
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-06-21T14:00", "2021-06-21T17:00"),  Collections.emptySet());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_sections_on_the_same_day_and_conflicting_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1231231", "MATH1", 3).setHasExam(true);
        Course phys2 = new Course("3453456", "PHYS2", 3).setHasExam(true);
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T08:00", "2021-06-21T11:00"),  Collections.emptySet());
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-06-21T09:30", "2021-06-21T13:00"),  Collections.emptySet());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_may_have_sections_on_the_different_days_and_conflicting_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1231231", "MATH1", 3).setHasExam(true);
        Course phys2 = new Course("3453456", "PHYS2", 3).setHasExam(true);
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T08:00", "2021-06-21T11:00"),  Collections.emptySet());
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-06-23T09:30", "2021-06-23T13:00"),  Collections.emptySet());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_may_have_sections_intersecting_at_one_time() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1231231", "MATH1", 3).setHasExam(true);
        Course phys2 = new Course("3453456", "PHYS2", 3).setHasExam(true);
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T11:00", "2021-06-21T13:00"),  Collections.emptySet());
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-06-21T13:00", "2021-06-21T16:00"),  Collections.emptySet());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_one_section_when_its_exam_time_is_colliding_with_two_other_sections() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3).setHasExam(true);
        Course ap = new Course("2222222", "AP", 4).setHasExam(true);
        Course phys2 = new Course("3333333", "PHYS2", 3).setHasExam(true);
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T08:00", "2021-06-21T11:30"), Collections.emptySet());
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-06-21T11:00", "2021-06-21T14:00"),  Collections.emptySet());
        Section ap1_1 = new Section(ap, "01", new ExamTime("2021-06-21T13:30", "2021-06-21T16:30"),  Collections.emptySet());
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, ap1_1, phys2_1);
        assertThat(list1.checkExamTimeConflicts())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Students_cant_take_less_than_twelve_credits() throws Exception {
        Course testCourse = new Course("1111111", "Test", 3).setHasExam(false);
        Student testStudent = new Student("1", "ali");
        EnrollmentList list1 = new EnrollmentList("list", testStudent);
        list1.addSections(new Section(testCourse, "01"));
        assertThat(list1.checkValidGPALimit())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_conflict_in_section_schedules() throws Exception {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1111111", "MATH1", 3).setHasExam(true);
        Course phys2 = new Course("3333333", "PHYS2", 3).setHasExam(true);
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Monday", "11:00", "13:00");
        PresentationSchedule p4 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        Section math1_1 = new Section(math1, "01", new ExamTime("2021-06-21T08:00", "2021-06-21T11:30"), Set.of(p1, p2));
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p3, p4));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(math1_1, phys2_1);
        assertThat(list1.checkSectionScheduleConflicts())
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void Enrollment_list_with_no_violation_is_created_correctly() throws Exception {
        Student bebe = mock(Student.class);
        Course ap = new Course("2222222", "AP", 4).setHasExam(true);
        Course phys2 = new Course("3333333", "PHYS2", 4).setHasExam(true);
        Course da = new Course("4444444", "DA", 4).setHasExam(true);
        PresentationSchedule p1 = new PresentationSchedule("Monday", "10:30", "12:00");
        PresentationSchedule p2 = new PresentationSchedule("Wednesday", "10:30", "12:00");
        PresentationSchedule p3 = new PresentationSchedule("Tuesday", "10:30", "12:00");
        Section phys2_1 = new Section(phys2, "01", new ExamTime("2021-07-21T11:00", "2021-07-21T14:00"), Set.of(p1));
        Section ap1_1 = new Section(ap, "01", new ExamTime("2021-08-21T13:30", "2021-08-21T16:30"), Set.of(p2));
        Section da_1 = new Section(da, "01", new ExamTime("2021-09-21T13:30", "2021-09-21T16:30"), Set.of(p3));
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        when(bebe.calculateGPA()).thenReturn(new Grade(18));
        list1.addSections(ap1_1, phys2_1, da_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void Number_of_new_conflicts_are_zero_when_examTime_set_to_time_that_has_no_conflicts_with_others() throws Exception {
        Student bebe = mock(Student.class);
        Course da = new Course("6666666", "DA", 4).setHasExam(true);
        Course ds = new Course("7777777", "DS", 4).setHasExam(true);
        Course dm = new Course("8888888", "DM", 4).setHasExam(true);
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
        Student bebe = mock(Student.class);
        Course da = new Course("6666666", "DA", 4).setHasExam(true);
        Course ds = new Course("7777777", "DS", 4).setHasExam(true);
        Course dm = new Course("8888888", "DM", 4).setHasExam(true);
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
        Student bebe = mock(Student.class);
        Course da = new Course("6666666", "DA", 4).setHasExam(true);
        Course ds = new Course("7777777", "DS", 4).setHasExam(true);
        Course dm = new Course("8888888", "DM", 4).setHasExam(true);
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
        Student bebe = mock(Student.class);
        Course da = new Course("6666666", "DA", 4).setHasExam(true);
        Course ds = new Course("7777777", "DS", 4).setHasExam(true);
        Course dm = new Course("8888888", "DM", 4).setHasExam(true);
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
        Student bebe = mock(Student.class);
        Course da = new Course("6666666", "DA", 4).setHasExam(true);
        Course ds = new Course("7777777", "DS", 4).setHasExam(true);
        Course dm = new Course("8888888", "DM", 4).setHasExam(true);
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

